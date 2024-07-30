package org.protelis.demo;

import com.uchuhimo.konf.ConfigSpec;
import com.uchuhimo.konf.RequiredItem;
import org.protelis.demo.data.ProtelisNode;

import java.util.List;

/**
 * Konf specification for the TOML configuration file.
 */
public final class ProtelisConfigSpec {

    /**
     * Create a new config specification named protelis.
     */
    public static final ConfigSpec SPEC = new ConfigSpec("protelis");
    /**
     * Required field iterations. It represents the number of cycles the simulation will perform.
     */
    @SuppressWarnings("PMD.FieldNamingConventions")
    // CHECKSTYLE: ConstantName OFF
    public static final RequiredItem<Integer> iterations = new RequiredItem<>(SPEC, "iterations") { };
    /**
     * Required field protelisModuleName. It is the name of the .pt source file.
     */
    @SuppressWarnings("PMD.FieldNamingConventions")
    // CHECKSTYLE: ConstantName OFF
    public static final RequiredItem<String> protelisModuleName = new RequiredItem<>(SPEC, "protelisModuleName") { };
    /**
     * Required field nodes. It contains a list of protelis nodes.
     */
    @SuppressWarnings("PMD.FieldNamingConventions")
    // CHECKSTYLE: ConstantName OFF
    public static final RequiredItem<List<ProtelisNode>> nodes = new RequiredItem<>(SPEC, "nodes") { };

    private ProtelisConfigSpec() { }
}
