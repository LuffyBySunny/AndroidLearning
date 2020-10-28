package com.genshuiue.student.jaeger

import com.genshuixue.student.jaeger.JaegerUtil
import io.opentracing.Tracer
import io.opentracing.util.GlobalTracer
import org.junit.Test

/**
 * created by sunshuo
 * on 2020/10/28
 */
class JaegerTest {
    @Test
    fun jaegerTest() {
        JaegerUtil.getTracer()
        val totalSpan = GlobalTracer.get().buildSpan("student").start()

        totalSpan.log("start")
        for (i in 0..1) {
            val span = GlobalTracer.get().buildSpan("student").asChildOf(totalSpan).start()
            span.log("spanStart")
            Thread.sleep(1000)
            span.log("spanFinish")
            span.finish()
        }
        totalSpan.log("end")
        totalSpan.finish()
    }
}