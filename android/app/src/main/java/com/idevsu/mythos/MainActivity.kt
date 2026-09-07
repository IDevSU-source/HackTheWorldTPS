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
    private lateinit var data: JSONObject
    private var xp = 0
    private val prefs by lazy { getSharedPreferences("mythos", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xp = prefs.getInt("xp", 0)
        loadCorpus()
        showHome()
    }

    private fun loadCorpus() {
        val raw = assets.open("corpus_manifest.json").bufferedReader().use { it.readText() }
        data = JSONObject(raw)
    }

    private fun base() = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(28,40,28,28); setBackgroundColor(Color.BLACK) }
    private fun text(v:String,s:Float=16f)=TextView(this).apply { text=v; textSize=s; setTextColor(Color.WHITE); setPadding(0,8,0,8) }
    private fun button(v:String,a:()->Unit)=Button(this).apply { text=v; setOnClickListener{a()} }

    private fun showHome() {
        val r=base()
        r.addView(text("MYTHOS: HACK THE WORLD",28f).apply{typeface=Typeface.DEFAULT_BOLD;gravity=Gravity.CENTER})
        r.addView(text("TRILLIONS PER SECOND // INSTALLATION",13f).apply{gravity=Gravity.CENTER})
        val c=data.getJSONObject("corpus")
        r.addView(text("LEVEL "+level()+"    XP "+xp+"\nCORPUS "+c.getInt("file_count")+" FILES"))
        r.addView(button("▶ CONTINUE CAMPAIGN"){showCampaign()})
        r.addView(button("⌘ CODEX"){showCodex()})
        r.addView(button("◈ KNOWLEDGE GRAPH"){showGraph()})
        r.addView(button("▣ SYSTEM STATUS"){showStatus()})
        setContentView(r)
    }

    private fun showCampaign() {
        val r=base(); r.addView(text("CAMPAIGN",26f))
        val a=data.getJSONArray("sections")
        for(i in 0 until a.length()){val s=a.getJSONObject(i); r.addView(button(s.getString("title")+" • "+sectionXp(s.getString("id"))+" XP"){showEntriesFor(s.getString("title"),s.getJSONArray("types"))})}
        r.addView(button("← HOME"){showHome()}); setContentView(ScrollView(this).apply{addView(r)})
    }

    private fun showEntriesFor(section:String,types:JSONArray){
        val r=base(); r.addView(text(section,24f)); val es=data.getJSONArray("entries")
        for(i in 0 until es.length()){val e=es.getJSONObject(i); var ok=false; for(j in 0 until types.length()) if(e.getString("type")==types.getString(j)) ok=true; if(ok) r.addView(button(e.getString("title")+"  +"+e.getInt("xp")+" XP"){readEntry(e)})}
        r.addView(button("← CAMPAIGN"){showCampaign()}); setContentView(ScrollView(this).apply{addView(r)})
    }

    private fun readEntry(e:JSONObject){
        val r=base(); r.addView(text(e.getString("title"),25f)); r.addView(text(e.getString("source")+"\n"+e.getInt("words")+" words • "+e.getInt("xp")+" XP",12f))
        val body=assets.open("corpus/"+e.getString("source")).bufferedReader().use{it.readText()}
        r.addView(ScrollView(this).apply{addView(text(body,15f))},LinearLayout.LayoutParams(-1,0,1f))
        r.addView(button("✓ COMPLETE +"+e.getInt("xp")+" XP"){val k="done_"+e.getString("id"); if(!prefs.getBoolean(k,false)){xp+=e.getInt("xp");prefs.edit().putBoolean(k,true).putInt("xp",xp).apply()};showHome()})
        setContentView(r)
    }

    private fun showCodex(){val r=base();r.addView(text("CODEX",26f));val n=data.getJSONObject("graph").getJSONArray("nodes");for(i in 0 until n.length()){val x=n.getJSONObject(i);if(x.getString("type")=="concept")r.addView(text("◇ "+x.getString("label")))};r.addView(button("← HOME"){showHome()});setContentView(ScrollView(this).apply{addView(r)})}
    private fun showGraph(){val r=base();val g=data.getJSONObject("graph");r.addView(text("KNOWLEDGE GRAPH",26f));r.addView(text(g.getJSONArray("nodes").length().toString()+" nodes\n"+g.getJSONArray("edges").length()+" connections"));r.addView(text("Generated from the complete TPS corpus. Source files become knowledge nodes; concepts and explicit links become relationships."));r.addView(button("← HOME"){showHome()});setContentView(r)}
    private fun showStatus(){val r=base();val c=data.getJSONObject("corpus");r.addView(text("SYSTEM STATUS",26f));r.addView(text("MYTHOS: HACK THE WORLD\n\nCORPUS FILES: "+c.getInt("file_count")+"\nCORPUS WORDS: "+c.getInt("word_count")+"\nCURRENT XP: "+xp+"\nLEVEL: "+level()+"\n\nINSTALLATION ENGINE: ONLINE"));r.addView(button("← HOME"){showHome()});setContentView(r)}

    private fun sectionXp(id:String):Int{
        val types=when(id){"boot"->setOf("orientation","system");"diagnostic"->setOf("mission","checkpoint");"firewall"->setOf("mission","system");"overclock"->setOf("checkpoint","mission");"defrag"->setOf("mission","checkpoint");"reboot"->setOf("checkpoint","system");else->setOf("codex","reference")}
        var total=0;val es=data.getJSONArray("entries");for(i in 0 until es.length())if(types.contains(es.getJSONObject(i).getString("type")))total+=es.getJSONObject(i).getInt("xp");return total
    }
    private fun level()=1+xp/500
}