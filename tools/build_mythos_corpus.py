#!/usr/bin/env python3
from pathlib import Path
import hashlib, json, re

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "android/app/src/main/assets"
CORPUS = OUT / "corpus"
EXTS = {".md", ".markdown", ".txt"}
CONCEPTS = [
    "flux","voxel","latency","rootkit","resource hog","null pointer","attention",
    "signal lock","packet switching","firewall","overclock","defrag","zero lag",
    "reboot","equanimity","mindfulness","anatta","dharma","sangha","buddha",
    "observer","render engine","handshake","self","craving","aversion","attachment",
    "TPS0","AI alignment","hermeticism","gnosticism","stoicism","zen buddhism",
]

def rel(p): return p.relative_to(ROOT).as_posix()
def title(text, path):
    m = re.search(r"^#\s+(.+)$", text, re.M)
    return m.group(1).strip() if m else Path(path).stem.replace("_"," ").replace("-"," ").title()
def headings(text):
    return [m.group(2).strip() for m in re.finditer(r"^(#{1,6})\s+(.+)$", text, re.M)]
def concepts(text):
    low=text.lower()
    return sorted({c for c in CONCEPTS if c.lower() in low})
def classify(path, text):
    p=path.lower()
    if "checkpoint" in p: return "checkpoint"
    if p == "readme.md" or "waking_up" in p or "introduction" in p: return "orientation"
    if "personal_codex" in p or p.startswith("api/") or "lexicon" in p or "references" in p: return "codex"
    if "spec" in p or "architecture" in p or "manifest" in p or "protocol" in p: return "system"
    return "mission"
def xp(kind, words):
    return {"orientation":50,"mission":100,"checkpoint":250,"system":125,"codex":100}.get(kind,50)+min(250,(words//250)*25)

def main():
    pairs=[]
    for base in [ROOT/"src", ROOT/"api"]:
        if base.exists():
            pairs += [(p,p.read_text(encoding="utf-8")) for p in base.rglob("*") if p.is_file() and p.suffix.lower() in EXTS]
    readme=ROOT/"README.md"
    if readme.exists(): pairs.insert(0,(readme,readme.read_text(encoding="utf-8")))
    entries=[]; nodes={}; edges=set(); by_path={}
    CORPUS.mkdir(parents=True,exist_ok=True)

    for p,text in pairs:
        rp=rel(p); eid=hashlib.sha1(rp.encode()).hexdigest()[:12]
        w=len(re.findall(r"\b[\w’'-]+\b",text)); cs=concepts(text)
        dest=CORPUS/rp; dest.parent.mkdir(parents=True,exist_ok=True); dest.write_text(text,encoding="utf-8")
        e={"id":eid,"source":rp,"title":title(text,rp),"type":classify(rp,text),"words":w,"xp":xp(classify(rp,text),w),"headings":headings(text),"concepts":cs}
        entries.append(e); by_path[rp]=eid; nodes[eid]={"id":eid,"label":e["title"],"type":e["type"]}
        for c in cs:
            cid="concept:"+re.sub(r"[^a-z0-9]+","-",c.lower()).strip("-")
            nodes[cid]={"id":cid,"label":c,"type":"concept"}; edges.add((eid,cid,"explains"))

    for p,text in pairs:
        src=rel(p)
        for target in re.findall(r"\[[^\]]+\]\(([^)]+)\)",text):
            if target.startswith(("http://","https://")): continue
            candidate=str(Path(src).parent/target).replace("\\","/")
            if candidate in by_path: edges.add((by_path[src],by_path[candidate],"links"))

    sections=[
      {"id":"boot","title":"BOOT","order":0,"requires":[],"types":["orientation","system"]},
      {"id":"diagnostic","title":"SYSTEM DIAGNOSTIC","order":1,"requires":["boot"],"types":["mission","checkpoint"]},
      {"id":"firewall","title":"FIREWALL","order":2,"requires":["diagnostic"],"types":["mission","system"]},
      {"id":"overclock","title":"OVERCLOCK","order":3,"requires":["firewall"],"types":["checkpoint","mission"]},
      {"id":"defrag","title":"DEFRAG","order":4,"requires":["overclock"],"types":["mission","checkpoint"]},
      {"id":"reboot","title":"REBOOT","order":5,"requires":["defrag"],"types":["checkpoint","system"]},
      {"id":"codex","title":"CODEX","order":6,"requires":["reboot"],"types":["codex","reference"]},
    ]
    payload={"game":"MythOS: Hack The World","version":1,"corpus":{"file_count":len(entries),"word_count":sum(e["words"] for e in entries)},"entries":entries,"graph":{"nodes":list(nodes.values()),"edges":[{"from":a,"to":b,"type":c} for a,b,c in sorted(edges)]},"sections":sections}
    OUT.mkdir(parents=True,exist_ok=True)
    (OUT/"corpus_manifest.json").write_text(json.dumps(payload,indent=2,ensure_ascii=False),encoding="utf-8")
    print(f"MYTHOS CORPUS: {len(entries)} files / {payload['corpus']['word_count']} words / {len(nodes)} nodes / {len(edges)} edges")

if __name__=="__main__": main()
