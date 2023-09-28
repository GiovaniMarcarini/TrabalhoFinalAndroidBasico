package br.edu.utfpr.trabalhofinalandroidbasico

import android.content.ContentValues
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var etCodigo : EditText
    private lateinit var etCidade : EditText
    private lateinit var etLitros : EditText
    private lateinit var btIncluir : Button
    private lateinit var btPesquisar : Button
    private lateinit var btEstatistica : Button
    private lateinit var lCicade : TextView
    private lateinit var lQuantidade : TextView

    val tiposCombustiveis = arrayOf(
        "1 - Gasolina",
        "2 - Etanol",
        "3 - Diesel",
        "4 - Gás Natural"
    )

    private lateinit var bd : SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btIncluir = findViewById(R.id.btIncluir)
        btPesquisar = findViewById(R.id.btPesquisar)
        btEstatistica = findViewById(R.id.btEstatistica)
        etCodigo = findViewById(R.id.etCodigo)
        etCidade = findViewById(R.id.etCidade)
        etLitros = findViewById(R.id.etLitros)
        lCicade = findViewById(R.id.lCidade)
        lQuantidade = findViewById(R.id.lQuantidade)

        viewInvisible()

        bd = SQLiteDatabase.openOrCreateDatabase(this.getDatabasePath("combustivel.sqlite"), null)

        bd.execSQL("CREATE TABLE IF NOT EXISTS abastecimento( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
               "codigo TEXT, cidade TEXT, litros TEXT)")
    }

    fun btPesquisarOnClick(view: View) {
        val cod = etCodigo.text.toString()
        if (cod.isEmpty() ){
            Toast.makeText(this, "Código do combustível não localizado", Toast.LENGTH_SHORT).show()
            return
        }
        val exists = checkCodigoCombustivel(cod)
        if (exists) {
            viewVisible()
        } else {
            Toast.makeText(this, "Código do combustível não localizado", Toast.LENGTH_SHORT).show()
            return
        }
    }

    fun btInfoOnClick(view: View){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tipos de Combustível")
        builder.setItems(tiposCombustiveis, null)
        builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun btIncluirOnClick(view: View){
        val registro = ContentValues()
        registro.put("codigo", etCodigo.text.toString())
        registro.put("cidade", etCidade.text.toString())
        registro.put("litros", etLitros.text.toString())
        bd.insert("abastecimento", null, registro)
        val total = totalRegistros()
        Toast.makeText(this, "Abastecimento Registrado \nQtd. Abastecimentos: $total", Toast.LENGTH_SHORT).show()
        etCidade.setText("")
        etLitros.setText("")
        viewInvisible()
    }
    fun btEstatisticaOnClick(view: View){
        val cod = etCodigo.text.toString()
        val registros = bd.query("abastecimento", null, "codigo=${cod}", null,
            null, null,null)

        var total = 0.0

        while(registros.moveToNext()){
            total += registros.getString(3).toDouble()
        }
        val nome = checkNomeCombustivel(cod)
        Toast.makeText(this, "$nome - Total de litros: $total", Toast.LENGTH_SHORT).show()
        viewInvisible()
    }

    private fun totalRegistros(): Int {
        val registros = bd.query("abastecimento", null, null, null,
            null, null,null)
        val conter = registros.count
        registros.close()

        return conter
    }

    private fun checkCodigoCombustivel(codigo: String): Boolean {

        for (combustivel in tiposCombustiveis) {
            if (combustivel.contains(codigo)) {
                return true
            }
        }
        return false
    }

    private fun checkNomeCombustivel(codigo: String): String {

        for (combustivel in tiposCombustiveis) {
            if (combustivel.contains(codigo)) {
                return combustivel
            }
        }
        return "Dados não encontrados"
    }

    fun viewVisible(){
        etCidade.visibility = View.VISIBLE
        etLitros.visibility = View.VISIBLE
        btIncluir.visibility = View.VISIBLE
        btEstatistica.visibility = View.VISIBLE
        lCicade.visibility = View.VISIBLE
        lQuantidade.visibility = View.VISIBLE
    }
    fun viewInvisible(){
        etCidade.visibility = View.INVISIBLE
        etLitros.visibility = View.INVISIBLE
        btIncluir.visibility = View.INVISIBLE
        btEstatistica.visibility = View.INVISIBLE
        lCicade.visibility = View.INVISIBLE
        lQuantidade.visibility = View.INVISIBLE
    }
}