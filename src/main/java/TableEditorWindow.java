import com.vaadin.client.ui.Field;
import com.vaadin.client.ui.VDateField;
import com.vaadin.client.ui.VTextField;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static com.vaadin.ui.Alignment.*;

/**
 * Created by virtual on 20.06.2015.
 */
public  abstract class TableEditorWindow  extends  ActionWindow{
    protected Set<Object> dates2 = new HashSet<Object>();
    protected Map<Object, Component> map = new HashMap<Object, Component>();
    private final Button save;


    protected void  replaceToCombo(String key, String query){
        Container container = new HierarchicalContainer();
        container.addContainerProperty("id", String.class, "");
        String defaultValue = null;
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:ivan.db3");
            try{
               ResultSet rs = connection.createStatement().executeQuery(query);
                while (rs.next()){
                    defaultValue = rs.getString(1);
                    container.addItem(defaultValue).getItemProperty("id").setValue(defaultValue);
                }
            }finally{
                connection.close();
            }
        }catch (Exception ex){

        }

        replaceToCombo(key, container, defaultValue);

    }

    protected void replaceToCombo(String key, Container container, String defaultValue){
        final TextField  field = (TextField)map.get(key);
        HorizontalLayout layout = (HorizontalLayout) field.getParent();
        field.setVisible(false);
        final ComboBox comboBox = new ComboBox("",container);
        comboBox.setImmediate(true);
        comboBox.setNullSelectionAllowed(false);

        comboBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                field.setValue((String) comboBox.getValue());
            }
        });

        if(container.containsId(field.getValue())) {
            comboBox.select(field.getValue());
        }else{
            field.setValue(defaultValue);
            comboBox.select(defaultValue);
        }
        layout.addComponent(comboBox, 1);
    }


    private Set<AbstractField> errors = new HashSet<AbstractField>();

    private void setNotNull(final AbstractField field){
        field.setImmediate(true);
        if(nullorempty(field.getValue())){
            errors.add(field);
            save.setEnabled(false);
        }

        field.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(nullorempty(field.getValue())) errors.add(field);
                else errors.remove(field);
                save.setEnabled(errors.isEmpty());

            }
        });

    }

    private boolean nullorempty(Object value){
        if(value == null) return true;
        if(value instanceof  String){
            String s = ((String)value).trim();
            if(s.length() ==0) return true;
        }
        return false;
    }

    protected void setNotNull(String key){
        if(map.containsKey(key)) setNotNull((AbstractField)map.get(key));
    }


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

        save = new Button("Записать");
       content.addComponent(save);
       save.addClickListener(new Button.ClickListener() {
           @Override
           public void buttonClick(Button.ClickEvent clickEvent) {
               if (actionPerformed()) close();
           }
       });

        setContent(content);
    }

    protected abstract boolean actionPerformed();


}
