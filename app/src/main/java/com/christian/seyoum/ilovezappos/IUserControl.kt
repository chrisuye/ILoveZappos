package com.christian.seyoum.ilovezappos

interface IUserControl {
    fun add(user: User)
    fun getUser():MutableList<User>

    val users:IUserRepo
}