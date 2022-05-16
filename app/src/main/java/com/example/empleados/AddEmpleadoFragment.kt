package com.example.empleados

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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

class AddEmpleadoFragment : Fragment() {
    private var param1: String? = null
    private lateinit var imgAvatar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val vista = inflater.inflate(R.layout.fragment_add_empleado, container, false)

        vista.findViewById<Button>(R.id.add_empleado_guardar).setOnClickListener{ Agregar() }

        imgAvatar = vista.findViewById(R.id.add_empleado_picture)
        imgAvatar.setOnClickListener{
            var gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
        }

        return vista
    }

    fun Agregar(){

        val empleadoId = view?.findViewById<TextView>(R.id.add_empleado_id)
        val empleadoNombre = view?.findViewById<TextView>(R.id.add_empleado_nombre)
        val empleadoPuesto = view?.findViewById<TextView>(R.id.add_empleado_puesto)
        val empleadoDepartamento = view?.findViewById<TextView>(R.id.add_empleado_departamento)
        val idEmpleado : Int = EmpleadoRepository.instance.datos().size+1
        val avatar:String = encodeImage(imgAvatar.drawable.toBitmap()).toString()
        val empleado = Empleado (idEmpleado, empleadoId?.text.toString(), empleadoNombre?.text.toString(), empleadoPuesto?.text.toString(), empleadoDepartamento
            ?.text.toString(), avatar)
        empleado.let { EmpleadoRepository.instance.save(it) }

        Salir()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            val imageUri = data?.data
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
        fun newInstance(param1: String) =
            AddEmpleadoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}