package com.demo.learnify

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LearnifyApplication

fun main(args: Array<String>) {
	runApplication<LearnifyApplication>(*args)
}
