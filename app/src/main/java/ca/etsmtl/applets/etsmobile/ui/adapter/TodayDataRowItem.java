package ca.etsmtl.applets.etsmobile.ui.adapter;

/**
 * Created by laurencedevillers on 14-10-22.
 */
public class TodayDataRowItem {


    public static enum viewType {
        VIEW_TYPE_TITLE_EVENT(0), VIEW_TYPE_EVENT(1), VIEW_TYPE_TITLE_SEANCE(2), VIEW_TYPE_SEANCE(3), VIEW_TYPE_ETS_EVENT(4);
        private final int value;

        private viewType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public Object data;
    public int type;

    public TodayDataRowItem(viewType type) {
        this(type, null);
    }

    public TodayDataRowItem(viewType type, Object data) {
        this.type = type.getValue();
        this.data = data;
    }
}
