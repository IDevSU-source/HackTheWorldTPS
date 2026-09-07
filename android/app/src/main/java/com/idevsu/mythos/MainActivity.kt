package com.idevsu.mythos
import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*
import org.json.JSONObject
import org.json.JSONArray

class MainActivity : Activity() {
 private lateinit var data:JSONObject
 private var xp=0
 private val prefs by lazy{getSharedPreferences("mythos",MODE_PRIVATE)}
 override fun onCreate(b:Bundle?){super.onCreate(b);xp=prefs.getInt("xp",0);load();home()}
 private fun load(){data=JSONObject(assets.open("corpus_manifest.json").bufferedReader().use{it.readText()})}
 private fun root()=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(28,32,28,28);setBackgroundColor(Color.rgb(8,10,14))}
 private fun txt(v:String,s:Float=16f)=TextView(this).apply{text=v;textSize=s;setTextColor(Color.WHITE);setPadding(0,8,0,8)}
 private fun btn(v:String,f:()->Unit)=Button(this).apply{text=v;setOnClickListener{f()}}
 private fun done(id:String)=prefs.getBoolean("done_"+id,false)
 private fun home(){
  val r=root();r.addView(txt("MYTHOS",34f).apply{gravity=Gravity.CENTER;typeface=Typeface.DEFAULT_BOLD});r.addView(txt("HACK THE WORLD",20f).apply{gravity=Gravity.CENTER})
  val c=data.getJSONObject("corpus");r.addView(txt("LEVEL "+level()+" • XP "+xp+"\n"+completed()+" / "+c.getInt("file_count")+" SOURCE NODES SYNCHRONIZED",17f))
  r.addView(btn("▶ ENTER CAMPAIGN"){campaign()});r.addView(btn("⌘ OPEN CODEX"){codex()});r.addView(btn("◈ MAP KNOWLEDGE GRAPH"){graph()});r.addView(btn("▣ PLAYER STATUS"){status()})
  setContentView(ScrollView(this).apply{addView(r)})
 }
 private fun campaign(){
  val r=root();r.addView(txt("INSTALLATION CAMPAIGN",26f));r.addView(txt("Traverse the corpus in sequence. Every synchronized node awards XP. Checkpoints test recall."))
  val a=data.getJSONArray("sections");for(i in 0 until a.length()){val s=a.getJSONObject(i);val id=s.getString("id");val open=unlocked(id);r.addView(btn((if(open)"▶ "else"🔒 ")+s.getString("title")+"   "+progress(id)){if(open)entries(s.getString("title"),s.getJSONArray("types"),id)else Toast.makeText(this,"Complete the previous sector first.",Toast.LENGTH_SHORT).show()})}
  r.addView(btn("← HOME"){home()});setContentView(ScrollView(this).apply{addView(r)})
 }
 private fun find(id:String):JSONObject?{val a=data.getJSONArray("sections");for(i in 0 until a.length())if(a.getJSONObject(i).getString("id")==id)return a.getJSONObject(i);return null}
 private fun has(a:JSONArray,v:String):Boolean{for(i in 0 until a.length())if(a.getString(i)==v)return true;return false}
 private fun unlocked(id:String):Boolean{val o=listOf("boot","diagnostic","firewall","overclock","defrag","reboot","codex");val i=o.indexOf(id);return i<=0||complete(o[i-1])}
 private fun complete(id:String):Boolean{val s=find(id)?:return false;val es=data.getJSONArray("entries");var n=0;var d=0;for(i in 0 until es.length()){val e=es.getJSONObject(i);if(has(s.getJSONArray("types"),e.getString("type"))){n++;if(done(e.getString("id")))d++}};return n>0&&n==d}
 private fun progress(id:String):String{val s=find(id)?:return "0/0";val es=data.getJSONArray("entries");var n=0;var d=0;for(i in 0 until es.length()){val e=es.getJSONObject(i);if(has(s.getJSONArray("types"),e.getString("type"))){n++;if(done(e.getString("id")))d++}};return d.toString()+"/"+n}
 private fun entries(section:String,types:JSONArray,id:String){
  val r=root();r.addView(txt(section,26f));r.addView(txt(brief(id)));val es=data.getJSONArray("entries")
  for(i in 0 until es.length()){val e=es.getJSONObject(i);if(has(types,e.getString("type"))){val mark=if(done(e.getString("id")))"✓ "else"◇ ";r.addView(btn(mark+e.getString("title")+"  +"+e.getInt("xp")+" XP"){read(e,id)})}}
  r.addView(btn("← CAMPAIGN"){campaign()});setContentView(ScrollView(this).apply{addView(r)})
 }
 private fun brief(id:String)=when(id){"boot"->"Initialize the system: vocabulary, lineage, architecture, assumptions."
 "diagnostic"->"Inspect the system: physics, rendering, handshake, GUI, algorithms, rootkit, checkpoints."
 "firewall"->"Cross into code: permissions, network security, and defensive boundaries."
 "overclock"->"Increase throughput: boosting, staging, and the next checkpoint."
 "defrag"->"Integrate the pieces: zero-lag and system integration."
 "reboot"->"Reassemble the model from a new state."
 else->"Deep archive. Connect the concepts you encountered across the campaign."}
 private fun read(e:JSONObject,id:String){
  val r=root();r.addView(txt(e.getString("title"),25f));r.addView(txt(e.getString("source")+"\n"+e.getInt("words")+" words • "+e.getInt("xp")+" XP",12f))
  val cs=e.getJSONArray("concepts");if(cs.length()>0)r.addView(txt("SIGNALS: "+join(cs),12f))
  val body=assets.open("corpus/"+e.getString("source")).bufferedReader().use{it.readText()};r.addView(ScrollView(this).apply{addView(txt(body,15f))},LinearLayout.LayoutParams(-1,0,1f))
  if(done(e.getString("id")))r.addView(txt("✓ SYNCHRONIZED"))
  else r.addView(btn("SYNC NODE  +"+e.getInt("xp")+" XP"){award(e);if(e.getString("type")=="checkpoint")checkpoint(e,id)else entries(id.uppercase(),find(id)!!.getJSONArray("types"),id)})
  r.addView(btn("← BACK"){entries(id.uppercase(),find(id)!!.getJSONArray("types"),id)});setContentView(r)
 }
 private fun award(e:JSONObject){val id=e.getString("id");if(done(id))return;xp+=e.getInt("xp");prefs.edit().putBoolean("done_"+id,true).putInt("xp",xp).apply()}
 private fun checkpoint(e:JSONObject,id:String){
  val r=root();r.addView(txt("CHECKPOINT // "+e.getString("title"),25f));r.addView(txt("Prove you traversed the node."))
  val cs=e.getJSONArray("concepts");val answer=if(cs.length()>0)cs.getString(0)else e.getString("title").split(" ")[0];r.addView(txt("Enter one signal associated with this node:"))
  val input=EditText(this).apply{hint="concept";setTextColor(Color.WHITE);setHintTextColor(Color.GRAY)};r.addView(input)
  r.addView(btn("SUBMIT CHECKPOINT"){if(input.text.toString().trim().equals(answer,true)){xp+=75;prefs.edit().putInt("xp",xp).putBoolean("cp_"+e.getString("id"),true).apply();Toast.makeText(this,"CHECKPOINT PASSED  +75 XP",Toast.LENGTH_SHORT).show();entries(id.uppercase(),find(id)!!.getJSONArray("types"),id)}else Toast.makeText(this,"Not quite. Re-read the node.",Toast.LENGTH_SHORT).show()})
  r.addView(btn("RETURN TO MISSION"){entries(id.uppercase(),find(id)!!.getJSONArray("types"),id)});setContentView(ScrollView(this).apply{addView(r)})
 }
 private fun codex(){val r=root();r.addView(txt("CODEX",26f));r.addView(txt("Concepts extracted from the complete corpus."));val n=data.getJSONObject("graph").getJSONArray("nodes");for(i in 0 until n.length()){val x=n.getJSONObject(i);if(x.getString("type")=="concept")r.addView(txt("◇ "+x.getString("label")))};r.addView(btn("← HOME"){home()});setContentView(ScrollView(this).apply{addView(r)})}
 private fun graph(){val r=root();val g=data.getJSONObject("graph");r.addView(txt("KNOWLEDGE GRAPH",26f));r.addView(txt(g.getJSONArray("nodes").length().toString()+" nodes\n"+g.getJSONArray("edges").length()+" connections"));r.addView(txt("Source nodes connect to extracted concepts and explicit source links."));r.addView(btn("← HOME"){home()});setContentView(r)}
 private fun status(){val r=root();val c=data.getJSONObject("corpus");r.addView(txt("PLAYER STATUS",26f));r.addView(txt("LEVEL "+level()+"\nXP "+xp+"\nSYNCHRONIZED "+completed()+" / "+c.getInt("file_count")+"\n\nINSTALLATION ENGINE: ONLINE"));r.addView(btn("← HOME"){home()});setContentView(r)}
 private fun join(a:JSONArray):String{val x=mutableListOf<String>();for(i in 0 until a.length())x.add(a.getString(i));return x.joinToString(" • ")}
 private fun completed():Int{var n=0;val e=data.getJSONArray("entries");for(i in 0 until e.length())if(done(e.getJSONObject(i).getString("id")))n++;return n}
 private fun level()=1+xp/500
}