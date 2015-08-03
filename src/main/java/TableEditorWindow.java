import com.vaadin.client.ui.Field;
import com.vaadin.client.ui.VDateField;
import com.vaadin.client.ui.VTextField;
import com.vaadin.data.Item;
import com.vaadin.ui.*;

import java.util.*;

import static com.vaadin.ui.Alignment.*;

/**
 * Created by virtual on 20.06.2015.
 */
public  abstract class TableEditorWindow  extends  ActionWindow{
    protected Set<Object> dates2 = new HashSet<Object>();
    protected Map<Object, Component> map = new HashMap<Object, Component>();

    protected java.sql.Date getDate(String s){
       try {
           return new java.sql.Date(
                   ((DateField) map.get(s)).getValue().getTime()
           );
       }catch (Exception ex){
           return null;
       }
    }
    protected String getString(String s){
        return ((TextField)map.get(s)).getValue();
    }

    public TableEditorWindow(final Table table, final Object value, Object... dates ){

        for(Object o:dates) dates2.add(o);
        VerticalLayout content  = new VerticalLayout();
        Object[] columns = table.getVisibleColumns();
        String[] headers = table.getColumnHeaders();
        int n = columns.length;

        for(int i =0; i< n; ++i){
            HorizontalLayout lay = new HorizontalLayout();
            Label l = new Label(headers[i]);
            lay.addComponent(l);
            Component field= dates2.contains(columns[i])?new DateField():new TextField();

            lay.addComponent(field);
            map.put(columns[i], field);
            content.addComponent(lay);
            lay.setComponentAlignment(l, Alignment.MIDDLE_LEFT);
            lay.setComponentAlignment(field, Alignment.MIDDLE_RIGHT);
        }

       if(value!=null){
           Item item = table.getItem(value);
           for(Object o: columns){
               Object val = item.getItemProperty(o).getValue();
               if(dates2.contains(o)){
                   ((DateField)map.get(o)).setValue((Date)val);
               }else{
                   ((TextField)map.get(o)).setValue(val.toString());
               }
           }
       }

       Button save = new Button("Записать");
       content.addComponent(save);
       save.addClickListener(new Button.ClickListener() {
           @Override
           public void buttonClick(Button.ClickEvent clickEvent) {
              if(actionPerformed()) close();
           }
       });

        setContent(content);
    }

    protected abstract boolean actionPerformed();
}
