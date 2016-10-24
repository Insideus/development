package ar.com.fennoma.davipocket.model;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.ui.adapters.StoreItemDetailAdapter;

public class StoreItemDetailConfiguration implements StoreItemDetailAdapter.IShowableItemContainer {

    public static final int VARIANT = 0;
    public static final int MODIFIER = 1;
    public static final int ADDITIONAL = 2;

    private int kindOfConfiguration;
    private List<StoreConfiguration> configurations;

    public StoreItemDetailConfiguration(){
        this.configurations = new ArrayList<>();
        kindOfConfiguration = ADDITIONAL;
    }

    public StoreItemDetailConfiguration(StoreConfiguration configuration){
        this.configurations = new ArrayList<>();
        configurations.add(configuration);
        this.kindOfConfiguration = getConfigurationIdentifier(configuration);
    }

    private int getConfigurationIdentifier(StoreConfiguration configuration) {
        switch (configuration.getType()){
            case "VARIANT":{
                return VARIANT;
            }
            case "MODIFIER":{
                return MODIFIER;
            }
            case "ADDITIONAL":{
                return ADDITIONAL;
            }
        }
        return 0;
    }

    public void addConfiguration(StoreConfiguration configuration){
        configurations.add(configuration);
    }

    @Override
    public int getKindOfItems() {
        return kindOfConfiguration;
    }

    @Override
    public List<StoreConfiguration> getConfigurations() {
        return configurations;
    }
}
