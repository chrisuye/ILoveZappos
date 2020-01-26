package com.christian.seyoum.ilovezappos

interface IUserRepo {
    fun add(user: User)
    fun getUser():MutableList<User>
}