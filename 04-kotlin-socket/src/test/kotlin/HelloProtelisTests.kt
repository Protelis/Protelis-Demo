import com.uchuhimo.konf.Config
import demo.*
import io.kotlintest.Spec
import io.kotlintest.specs.StringSpec
import io.mockk.spyk
import io.mockk.verify
import org.protelis.lang.ProtelisLoader
import kotlin.streams.toList

class HelloProtelisTests : StringSpec() {

    private var devices: List<Device> = emptyList()
    private var speakers: List<Speaker> = emptyList()
    private val config = Config { addSpec(ProtelisConfigSpec) }
            .from.toml.resource("config.toml")
    private val protelisModuleName = config[ProtelisConfigSpec.protelisModuleName]
    private val iterations = config[ProtelisConfigSpec.iterations]
    private val nodes = config[ProtelisConfigSpec.nodes]
    private val leaders = config[ProtelisConfigSpec.nodes].stream()
            .filter{ it.leader }
            .map { it.id }
            .toList()
    override fun beforeSpec(spec: Spec) {
        nodes.forEach {
            val socketNetworkManager = SocketNetworkManager(IntDeviceUID(it.id), it.hostandport.port, it.neighbors).apply { listen() }
            val program = ProtelisLoader.parse(protelisModuleName)
            val s = spyk(ConsoleSpeaker())
            val d = Device(program, it.id, socketNetworkManager, s)
            if (it.leader) {
                d.deviceCapabilities.executionEnvironment.put("leader", true)
            }
            devices += d
            speakers += s
        }
        repeat(iterations) {
            devices.forEach { it.runCycle() }
        }
        devices.forEach { (it.netmgr as SocketNetworkManager).stop() }
    }

    init {
        "The leader count should be correct" {
            val messages = generateSequence(3f) { it - 1 }
                    .take(iterations)
                    .map { "The leader's count is: ${it}"}
                    .toList()
            leaders.stream()
                    .flatMap({ x -> messages.stream().map({ msg -> Pair<Int, String>(x, msg) }) })
                    .forEach { verify(exactly = 1) { speakers[it.first].announce((it.second)) } }
        }

        "The leaders should announce their id" {
            leaders.forEach { verify(exactly = iterations) { speakers[it].announce("The leader is at ${it}") } }
        }

        "The leader neighbors should say something" {
            leaders
                    .map { it }
                    .flatMap { listOf(
                            (it + nodes.size -1) % nodes.size,
                            (it + 1) % nodes.size) }
                    .distinct()
                    .forEach { verify(atLeast = 1) { speakers[it].announce("Hello from the leader to its neighbor at ${it}") } }
        }
    }
}