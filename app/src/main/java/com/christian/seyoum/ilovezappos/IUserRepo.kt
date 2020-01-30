package com.christian.seyoum.ilovezappos

interface IUserRepo {
    fun add(user: User)
    fun getUser():MutableList<User>
}

/*
using in UserRepo to get access to the SQlite data base
 */