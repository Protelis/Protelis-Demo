package demo;

import com.uchuhimo.konf.ConfigSpec;
import com.uchuhimo.konf.RequiredItem;
import demo.data.ProtelisNode;

import java.util.Set;

public class ProtelisConfigSpec {
    public static final ConfigSpec spec = new ConfigSpec("protelis");

    public static final RequiredItem<Integer> iterations =
            new RequiredItem<Integer>(spec, "iterations") {};

    public static final RequiredItem<String> protelisModuleName =
            new RequiredItem<String>(spec, "protelisModuleName") {};

    public static final RequiredItem<Set<ProtelisNode>> nodes =
            new RequiredItem<Set<ProtelisNode>>(spec, "nodes") {};
}