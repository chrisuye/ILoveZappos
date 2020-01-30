package com.christian.seyoum.ilovezappos

interface IUserControl {
    fun add(user: User)
    fun getUser():MutableList<User>

    val users:IUserRepo
}
/*
interface is using in BitCoinPrice to get access to the SQlite database
 */