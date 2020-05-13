package sample;

import java.util.HashMap;
import java.util.Map;

public class CategoryChooser {
    private Map<String, Contracts.ContractType> categories;

    public CategoryChooser() {
        this.categories = new HashMap<>();
        populate();
    }

    public void populate(){
        categories.put("Auksas", Contracts.ContractType.AUKSAS);
        categories.put("Daiktai", Contracts.ContractType.DAIKTAI);
        categories.put("Verslas", Contracts.ContractType.VERSLAS);
    }

    public Map<String, Contracts.ContractType> getCategories() {
        return categories;
    }
}
