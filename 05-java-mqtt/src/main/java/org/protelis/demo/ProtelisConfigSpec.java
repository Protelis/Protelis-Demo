package org.protelis.demo;

import com.uchuhimo.konf.ConfigSpec;
import com.uchuhimo.konf.RequiredItem;
import org.protelis.demo.data.MqttProtelisNode;

import java.util.List;

/**
 * Konf specification for the TOML configuration file.
 */
@SuppressWarnings("PMD.FieldNamingConventions")
public final class ProtelisConfigSpec {

    /**
     * Create a new config specification named protelis.
     */
    public static final ConfigSpec SPEC = new ConfigSpec("protelis");
    /**
     * Required field iterations. It represents the number of cycles the simulation will perform.
     */
    // CHECKSTYLE: ConstantName OFF
    public static final RequiredItem<Integer> iterations = new RequiredItem<>(SPEC, "iterations") { };
    /**
     * Required field protelisModuleName. It is the name of the .pt source file.
     */
    public static final RequiredItem<String> protelisModuleName =
        new RequiredItem<>(SPEC, "protelisModuleName") { };

    /**
     * Required field brokerHost. It is the IP address of the MQTT broker.
     */
    public static final RequiredItem<String> brokerHost = new RequiredItem<>(SPEC, "brokerHost") { };

    /**
     * Required field brokerPort. It is the port of the MQTT broker.
     */
    //@SuppressWarnings("PMD.FieldNamingConventions")
    // CHECKSTYLE: ConstantName OFF
    public static final RequiredItem<Integer> brokerPort = new RequiredItem<>(SPEC, "brokerPort") { };

    /**
     * Required field nodes. It contains a list of protelis nodes.
     */
    public static final RequiredItem<List<MqttProtelisNode>> nodes = new RequiredItem<>(SPEC, "nodes") { };

    private ProtelisConfigSpec() { }
}
