package data;

import org.json.JSONObject;

public class Channel implements JSONPopulator {
    private Units units;
    private Item item;

    public Units getUnits() {
        return units;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public void populate(JSONObject data) {

        units = new Units();
        //noinspection ConstantConditions
        units.populate(data.optJSONObject("units"));

        item = new Item();
        //noinspection ConstantConditions
        item.populate(data.optJSONObject("item"));

    }
}
