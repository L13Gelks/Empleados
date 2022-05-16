package com.example.empleados

import EmpleadoAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.example.empleados.entity.Empleado
import com.example.empleados.repository.EmpleadoRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val ARG_PARAM1 = "param1"

class CamaraFragment : Fragment() {
    private var param1: String? = null

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
        val vista = inflater.inflate(R.layout.fragment_camara, container, false)
        val empleadoList= vista.findViewById<ListView>(R.id.empleados_list)
        //val empleadoRepository = EmpleadoRepository()
        val empleadoAdapter = context?.let { EmpleadoAdapter(it, EmpleadoRepository.instance.datos()) }
        empleadoList.onItemClickListener=
            AdapterView.OnItemClickListener{ parent, view, position, id->
                val empleado = parent.getItemAtPosition(position)
                val fragmento: Fragment = EditEmpleadoFragment.newInstance(empleado as Empleado)
                fragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.home_content, fragmento)
                    ?.commit()
                activity?.title = "Empleado"
            }
        empleadoList.adapter = empleadoAdapter

        vista.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener{ Agregar() }


        return vista
    }
    fun Agregar(){
        val fragmento: Fragment = AddEmpleadoFragment.newInstance("Agregar")
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.home_content, fragmento)
            ?.commit()
        activity?.title = "Agregar"
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            CamaraFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}