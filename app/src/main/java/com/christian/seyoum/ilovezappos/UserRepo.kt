package com.christian.seyoum.ilovezappos

import android.content.Context

class UserRepo (context: Context):IUserRepo{
    private var prices:MutableList<User> = mutableListOf()
    private val db: IUserDataBase
    init {
        db = UserDataBase(context)
        prices.addAll(db.getFoods())
    }
    override fun add(user: User) {
        db.addFood(user)
        prices.clear()
        prices.addAll(db.getFoods())
    }

    override fun getUser(): MutableList<User> {
        return prices
    }

}