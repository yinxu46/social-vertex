package cn.net.polyglot.verticle

import cn.net.polyglot.config.defaultJsonObject
import cn.net.polyglot.utils.text
import cn.net.polyglot.utils.writeln
import io.vertx.core.Vertx
import io.vertx.core.net.NetClient
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import io.vertx.kotlin.core.DeploymentOptions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author zxj5470
 * @date 2018/7/9
 */

@RunWith(VertxUnitRunner::class)
class IMTcpServerVerticleTest {
  private lateinit var vertx: Vertx
  private lateinit var client: NetClient
  private val port = 8081

  @Before
  fun before(context: TestContext) {
    vertx = Vertx.vertx()
    client = vertx.createNetClient()
    val opt = DeploymentOptions(config = defaultJsonObject.apply { put("port", port) })
    vertx.deployVerticle(IMTcpServerVerticle::class.java.name, opt,context.asyncAssertSuccess())
  }

  @Test
  fun testApplication(context: TestContext) {
    val async = context.async()
    vertx.createNetClient().connect(port, "localhost") {
      if (it.succeeded()) {
        val socket = it.result()

        socket.handler {
          println(it.text())
        }

        var i = 0
        vertx.setPeriodic(2333L) {
          socket.writeln("""{"type":"search","id":"zxj5470"}""")
          if (i < 3) i++
          else async.complete()
        }
      }
    }
  }

  @After
  fun after(context: TestContext) {
    vertx.close(context.asyncAssertSuccess())
  }
}
