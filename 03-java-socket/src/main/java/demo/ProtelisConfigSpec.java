package demo;

import com.uchuhimo.konf.ConfigSpec;
import com.uchuhimo.konf.RequiredItem;
import demo.data.ProtelisNode;

import java.util.List;

/**
 * Konf specification for the TOML configuration file.
 */
public final class ProtelisConfigSpec {

    private ProtelisConfigSpec() { }

    /**
     * Create a new config specification named protelis.
     */
    public static final ConfigSpec SPEC = new ConfigSpec("protelis");
    /**
     * Required field iterations. It represents the number of cycles the simulation will perform.
     */
    // CHECKSTYLE: ConstantName OFF
    public static final RequiredItem<Integer> iterations =
            new RequiredItem<Integer>(SPEC, "iterations") { };
    /**
     * Required field protelisModuleName. It is the name of the .pt source file.
     */
    public static final RequiredItem<String> protelisModuleName =
            new RequiredItem<String>(SPEC, "protelisModuleName") { };
    /**
     * Required field nodes. It contains a list of protelis nodes.
     */
    public static final RequiredItem<List<ProtelisNode>> nodes =
            new RequiredItem<List<ProtelisNode>>(SPEC, "nodes") { };
    // CHECKSTYLE: ConstantName ON
}
