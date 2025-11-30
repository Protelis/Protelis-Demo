package org.protelis.demo

import org.protelis.lang.datatype.DeviceUID
import org.protelis.vm.CodePath
import org.protelis.vm.NetworkManager
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.Channels
import java.nio.channels.CompletionHandler
import java.util.concurrent.ExecutionException
import kotlin.concurrent.thread

/**
 * A [NetworkManager] implementation using sockets.
 */
class SocketNetworkManager(
    private val uid: DeviceUID,
    private val port: Int,
    private val neighbors: Set<IPv4Host>,
    private val address: String = "127.0.0.1",
) : NetworkManager {
    private var messages: Map<DeviceUID, Map<CodePath, Any>> = emptyMap()
    private var running = false

    /**
     * Opens a socket server and listens on a separate thread.
     */
    fun listen() {
        if (!running) {
            val server = AsynchronousServerSocketChannel.open()
            server.bind(InetSocketAddress(address, port))
            running = true
            thread {
                while (running) {
                    server.accept<Any>(
                        null,
                        object : CompletionHandler<AsynchronousSocketChannel, Any> {
                            override fun completed(
                                clientChannel: AsynchronousSocketChannel?,
                                attachment: Any?,
                            ) {
                                if (server.isOpen) {
                                    server.accept<Any>(null, this)
                                }
                                if (clientChannel != null && clientChannel.isOpen) {
                                    try {
                                        handleConnection(clientChannel)
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    } catch (e: ClassNotFoundException) {
                                        e.printStackTrace()
                                    }
                                }
                            }

                            override fun failed(
                                exc: Throwable,
                                attachment: Any?,
                            ) {
                                exc.printStackTrace()
                            }
                        },
                    )
                }
                try {
                    server.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun handleConnection(client: AsynchronousSocketChannel) {
        ObjectInputStream(Channels.newInputStream(client)).use {
            when (val received = it.readObject()) {
                is Map<*, *> -> {
                    received.forEach { src, msg -> receiveMessage(src as DeviceUID, msg as Map<CodePath, Any>) }
                }
            }
        }
    }

    /**
     * Called to gracefully stop the service.
     */
    fun stop() {
        running = false
    }

    private fun receiveMessage(
        src: DeviceUID,
        msg: Map<CodePath, Any>,
    ) {
        messages += Pair(src, msg)
    }

    override fun shareState(toSend: Map<CodePath, Any>) {
        val message = mapOf(Pair(uid, toSend))
        neighbors.forEach { n ->
            var client: AsynchronousSocketChannel? = null
            var oos: ObjectOutputStream? = null
            try {
                client =
                    checkNotNull(AsynchronousSocketChannel.open()) {
                        "Cannot open an asynchronous socket channel"
                    }
                val future = client.connect(InetSocketAddress(n.host, n.port))
                future.get()
                oos = ObjectOutputStream(Channels.newOutputStream(client))
                oos.writeObject(message)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } finally {
                try {
                    oos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    client?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getNeighborState(): Map<DeviceUID, Map<CodePath, Any>> = messages.apply { messages = emptyMap() }
}
