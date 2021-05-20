package com.redislabs.edu.redi2read.repositories

import com.redislabs.edu.redi2read.models.Cart
import com.redislabs.modules.rejson.JReJSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import java.util.stream.Collectors
import java.util.stream.StreamSupport


@Repository
class CartRepository(
    @Autowired val template: RedisTemplate<String, String>
    ): CrudRepository<Cart, String> {

    private val redisJson = JReJSON()
    private val idPrefix = Cart::class.java.name

    private fun redisSets() = template.opsForSet()

    private fun redisHash(): HashOperations<String, String?, String?> {
        return template.opsForHash()
    }

    override fun <S : Cart> save(cart: S): S {
        // set cart id
        if (cart.id == null) {
            cart.id = UUID.randomUUID().toString()
        }
        val key: String = getKey(cart)
        redisJson[key] = cart
        redisSets().add(idPrefix, key)
        redisHash().put("carts-by-user-id-idx", cart.userId, cart.id!!)
        return cart
    }

    override fun <S : Cart> saveAll(carts: Iterable<S>): Iterable<S> {
        return StreamSupport //
            .stream(carts.spliterator(), false) //
            .map { save(it) } //
            .collect(Collectors.toList())
    }

    override fun findById(id: String): Optional<Cart> {
        val cart = redisJson.get(getKey(id), Cart::class.java)
        return Optional.ofNullable(cart)
    }

    override fun existsById(id: String): Boolean {
        return template.hasKey(getKey(id))
    }

    override fun findAll(): Iterable<Cart> {
        val keys = redisSets().members(idPrefix)!!.stream().toArray { arrayOf(it.toString()) }
        return redisJson.mget(Cart::class.java, *keys) as Iterable<Cart>
    }

    override fun findAllById(ids: Iterable<String?>): Iterable<Cart> {
        val keys = StreamSupport.stream(ids.spliterator(), false) //
            .map { getKey(it) }
            .toArray { arrayOf(it.toString()) }
        return redisJson.mget(Cart::class.java, *keys) as Iterable<Cart>
    }

    override fun count(): Long {
        return redisSets().size(idPrefix)!!
    }

    override fun deleteById(id: String) {
        redisJson.del(getKey(id))
    }

    override fun delete(cart: Cart) {
        deleteById(cart.id!!)
    }

    override fun deleteAll(carts: Iterable<Cart>) {
        val keys = StreamSupport //
            .stream(carts.spliterator(), false) //
            .map { idPrefix + it.id } //
            .collect(Collectors.toList())
        redisSets().operations.delete(keys)
    }

    override fun deleteAll() {
        redisSets().operations.delete(redisSets().members(idPrefix)!!)
    }

    fun findByUserId(id: Long): Cart? {
        val cartId = redisHash()["carts-by-user-id-idx", id.toString()]
        return cartId?.let { findById(it).get() }
    }

    companion object {
        private val idPrefix = Cart::class.java.name

        fun getKey(cart: Cart): String {
            return String.format("%s:%s", idPrefix, cart.id)
        }

        fun getKey(id: String?) = String.format("%s:%s", idPrefix, id)
    }

}