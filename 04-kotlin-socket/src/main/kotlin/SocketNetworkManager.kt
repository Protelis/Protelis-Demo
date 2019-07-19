package demo

import org.protelis.lang.datatype.DeviceUID
import org.protelis.vm.CodePath
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

class SocketNetworkManager(private val uid: DeviceUID, private val port: Int, private val neighbors: Set<IPv4Host>) : MyNetworkManager {
    private val timeout = 1000
    private var toBeSent: Map<CodePath, Any> = emptyMap()
    private var messages: Map<DeviceUID, Map<CodePath, Any>> = emptyMap()
    private var running = false

    fun listen() {
        running = true
        val server = ServerSocket(port).also { it.soTimeout = timeout }
        thread {
            while (running) {
                try {
                    handleConnection(server.accept())
                } catch (e: SocketTimeoutException) { }
            }
        }
    }

    fun stop() {
        running = false
    }

    private fun handleConnection(client: Socket) {
        val received = ObjectInputStream(client.getInputStream()).readObject()
        if (received is Map<*, *>) {
             received.forEach { (src, msg) ->
                receiveMessage(src as DeviceUID, msg as Map<CodePath, Any>)
            }
        }
        client.close()
    }

    private fun receiveMessage(src: DeviceUID, msg: Map<CodePath, Any>) {
        messages += Pair(src, msg)
    }

    override fun sendMessages() {
        neighbors.forEach {
            val socket = Socket(it.host, it.port)
            val stream = ObjectOutputStream(socket.getOutputStream())
            val message = mapOf(Pair(uid, toBeSent))
            stream.writeObject(message)
            socket.close()
        }
    }

    override fun shareState(toSend: Map<CodePath, Any>) { toBeSent = toSend }

    override fun getNeighborState() : Map<DeviceUID, Map<CodePath, Any>> =
            messages.apply { messages = emptyMap() }
}