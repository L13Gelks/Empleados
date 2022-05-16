package com.example.empleados

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import com.example.empleados.entity.Empleado
import com.example.empleados.repository.EmpleadoRepository
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

private const val ARG_PARAM1 = "param1"
private const val PICK_IMAGE=30

class EditEmpleadoFragment : Fragment() {
    private var empleado: Empleado? = null
    lateinit var imgAvatar:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            empleado=it.get(ARG_PARAM1) as Empleado?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var view:View = inflater.inflate(R.layout.fragment_edit_empleado, container, false)

        val nombre = view.findViewById<TextView>(R.id.edit_empleado_nombre)
        nombre.text = empleado?.nombre

        val identificacion = view.findViewById<TextView>(R.id.edit_empleado_id)
        identificacion.text = empleado?.identificacion

        val puesto = view.findViewById<TextView>(R.id.edit_empleado_puesto)
        puesto.text = empleado?.puesto

        val departamento = view.findViewById<TextView>(R.id.edit_empleado_departamento)
        departamento.text = empleado?.departamento
        imgAvatar = view.findViewById(R.id.edit_empleado_picture)

        view.findViewById<Button>(R.id.edit_empleado_eliminar).setOnClickListener{ Eliminar(1) }
        view.findViewById<Button>(R.id.edit_empleado_editar).setOnClickListener{ Editar(identificacion.text.toString()) }

        if(empleado?.avatar != ""){
            imgAvatar.setImageBitmap(empleado?.avatar?.let { decodeImage(it) })
        }

        imgAvatar.setOnClickListener{
            var gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode== PICK_IMAGE && resultCode==RESULT_OK){
            var imageUri = data?.data

            Picasso.get()
                .load(imageUri)
                .resize(120,120)
                .centerCrop()
                .into(imgAvatar)
        }
    }

    private fun encodeImage(bm: Bitmap): String? {
        val arr = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, arr)
        val arrByte = arr.toByteArray()
        return Base64.encodeToString(arrByte, Base64.DEFAULT).replace("\n","")
    }

    private fun decodeImage (b64 : String): Bitmap{
        val imageBytes = Base64.decode(b64, 0)
        return  BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    fun Eliminar(identificacion:Int){
        val builder = AlertDialog.Builder(context)

        builder.setMessage("¿Desea eliminar el empleado?")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->
                empleado?.let { EmpleadoRepository.instance.delete(it) }
            }
            .setNegativeButton(
                "No"
            ) { _, _ ->

            }
        val alert = builder.create()

        alert.show()
    }

    fun Editar(identificacion:String){
        val builder = AlertDialog.Builder(context)

        val empleadoNombre = view?.findViewById<TextView>(R.id.edit_empleado_nombre)
        val empleadoId = view?.findViewById<TextView>(R.id.edit_empleado_id)
        val empleadoPuesto = view?.findViewById<TextView>(R.id.edit_empleado_puesto)
        val empleadoDepartamento = view?.findViewById<TextView>(R.id.edit_empleado_departamento)

        empleado?.nombre = empleadoNombre?.text.toString()
        empleado?.puesto = empleadoPuesto?.text.toString()
        empleado?.identificacion = empleadoId?.text.toString()
        empleado?.departamento = empleadoDepartamento?.text.toString()
        empleado?.avatar = encodeImage(imgAvatar.drawable.toBitmap())!!

        builder.setMessage("¿Desea editar el empleado?")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->
                empleado?.let { EmpleadoRepository.instance.edit(it) }
            }
            .setNegativeButton(
                "No"
            ) { _, _ ->

            }
        val alert = builder.create()

        alert.show()

        Salir()
    }

    fun Salir(){
        val fragmento: Fragment = CamaraFragment.newInstance("Camara")
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.home_content, fragmento)
            ?.commit()
        activity?.title = "Camara"
    }

    companion object {
        @JvmStatic
        fun newInstance(empleado: Empleado) =
            EditEmpleadoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, empleado)
                }
            }
    }
}