import com.uchuhimo.konf.Config
import demo.*
import io.kotlintest.Spec
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.spyk
import io.mockk.verify
import org.protelis.lang.ProtelisLoader

class KotlinSocketTest : StringSpec() {

    private var devices: List<Device> = emptyList()
    private var speakers: List<Speaker> = emptyList()
    private val config = Config { addSpec(ProtelisConfigSpec) }
            .from.toml.resource("config.toml")
    private val protelisModuleName = config[ProtelisConfigSpec.protelisModuleName]
    private val iterations = config[ProtelisConfigSpec.iterations]
    private val nodes = config[ProtelisConfigSpec.nodes]
    private val leaders = config[ProtelisConfigSpec.nodes]
            .filter{ it.leader }
            .map { it.id }

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

        "There should be at least 1 leader" {
            leaders.size shouldBeGreaterThan 0;
        }

        "The leader count should be correct" {
            val messages = generateSequence(3f) { it - 1  }
                    .take(3)
                    .map { "The leader's count is: ${it}"}
                    .toList()
            leaders.stream()
                    .flatMap({ x -> messages.stream().map({ msg -> Pair<Int, String>(x, msg) }) })
                    .forEach { id ->
                        verify(exactly = 1) { speakers[id.first].announce((id.second)) }
                    }
            val leftovers = config[ProtelisConfigSpec.iterations] - 3
            if (leftovers > 0) {
                leaders.forEach { id ->
                    verify(exactly = leftovers) { speakers[id].announce("The leader's count is: 0.0") }
                }
            }

        }

        "The leaders should announce their id" {
            leaders.forEach {
                id -> verify(exactly = iterations) {
                    speakers[id].announce("The leader is at ${id}")
                }
            }
        }

        "The leader neighbors should say something" {
            leaders
                    .flatMap { listOf(
                            (it + nodes.size -1) % nodes.size,
                            (it + 1) % nodes.size) }
                    .distinct()
                    .forEach { id -> verify(atLeast = 1) { speakers[id].announce("Hello from the leader to its neighbor at ${id}") } }
        }
    }
}