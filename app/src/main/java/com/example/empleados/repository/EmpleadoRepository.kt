package com.example.empleados.repository

import com.example.empleados.entity.Empleado
import com.example.empleados.service.EmpleadoService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EmpleadoRepository() {
    private val empleadoService : EmpleadoService

    companion object{
        @JvmStatic
        val instance by lazy{
            EmpleadoRepository().apply{}
        }
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://etiquicia.click/restAPI/api.php/records/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        empleadoService = retrofit.create(EmpleadoService::class.java)
    }

    fun save(empleado: Empleado){
        empleadoService.save(empleado).execute()
    }

    fun edit(empleado: Empleado){
        empleado.idEmpleado?.let { empleadoService.update(it, empleado).execute() }
    }

    fun delete (empleado: Empleado){
        empleado.idEmpleado?.let { empleadoService.delete(it).execute() }
    }

    fun datos():List<Empleado>{
        return empleadoService.getEmpleado().execute().body()!!.records
    }
}