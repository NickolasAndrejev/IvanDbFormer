import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.*;

import java.sql.*;
import java.util.Date;

/**
 * Created by virtual on 20.06.2015.
 */
public class OperInput  extends VerticalLayout {
    private  Container opContainer =  new HierarchicalContainer();
    private Table table;
    private void reloadContainer(){
        try{
            opContainer.removeAllItems();
            Connection connection = DriverManager.getConnection("jdbc:sqlite:ivan.db3");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select id, name, surname, grandname, birthday, startday, endday, phone from opers");
            while(rs.next()){
                Integer id = rs.getInt(1);
                Item item = opContainer.addItem(id);
                item.getItemProperty("id").setValue(id);
                item.getItemProperty("name").setValue(rs.getString(2));
                item.getItemProperty("surname").setValue(rs.getString(3));
                item.getItemProperty("grandname").setValue(rs.getString(4));
                item.getItemProperty("birthday").setValue(rs.getDate(5));
                item.getItemProperty("startday").setValue(rs.getDate(6));
                item.getItemProperty("endday").setValue(rs.getDate(7));
                item.getItemProperty("phone").setValue(rs.getString(8));
            }
            rs.close();
            statement.close();
            connection.close();
        }catch (Exception ignored){

        }
    }

    public OperInput(){

        opContainer.addContainerProperty("id", Integer.class, null);
        opContainer.addContainerProperty("name", String.class, "");
        opContainer.addContainerProperty("surname", String.class, "");
        opContainer.addContainerProperty("grandname", String.class, "");

        opContainer.addContainerProperty("birthday", Date.class, new Date());
        opContainer.addContainerProperty("startday", Date.class, new Date());
        opContainer.addContainerProperty("endday", Date.class, new Date());
        opContainer.addContainerProperty("phone", String.class, null);

        reloadContainer();
        final Button newOp  = new Button("Новый");
        final Button editOp = new Button("Изменить");
        final Button delOp = new Button("Удалить");
        final Button refrOp = new Button("Обновить");
        refrOp.setId("refresh_button");
        editOp.setEnabled(false);
        delOp.setEnabled(false);



        HorizontalLayout buttons = new HorizontalLayout(newOp, editOp, delOp, refrOp);
        addComponent(buttons);
        table = new Table("",opContainer);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setVisibleColumns("name", "surname", "grandname", "birthday", "startday", "endday", "phone");
        table.setColumnHeaders("имя", "фамилия", "отчество", "дата рождения", "дата начала работы", "дата окончания работы", "телефон");
        addComponent(table);

        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                boolean state = table.getValue() != null;
                editOp.setEnabled(state);
                delOp.setEnabled(state);
            }
        });


        delOp.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                QueryWindow window = new QueryWindow("Точно удалить?", "delete from opers where id = " + table.getValue());
                UI.getCurrent().addWindow(window);
            }
        });

        refrOp.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
               // Page.getCurrent().getJavaScript().execute("alert('обновление');");
                reloadContainer();
                table.refreshRowCache();
                table.requestRepaint();
            }
        });


        editOp.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                TableEditorWindow win = new TableEditorWindow(table, table.getValue(),"birthday", "startday", "endday"){
                    {
                        setNotNull("name");
                        setNotNull("surname");
                        setNotNull("grandname");
                        setNotNull("startday");
                    }
                    @Override
                    protected boolean actionPerformed() {
                        try {
                            Connection connection = DriverManager.getConnection("jdbc:sqlite:ivan.db3");
                            Integer id = (Integer)table.getValue();
                            try{
                                PreparedStatement ps = connection.prepareStatement("update opers set name=?, surname=?, grandname=?, birthday=? , startday=?, endday=?, phone =? where id =" + id);
                                ps.setString(1, getString("name"));
                                ps.setString(2, getString("surname"));
                                ps.setString(3, getString("grandname"));
                                ps.setDate(4, getDate("birthday"));
                                ps.setDate(5, getDate("startday"));
                                ps.setDate(6, getDate("endday"));
                                ps.setString(7, getString("phone"));
                                ps.execute();
                                ps.close();
                            }finally{
                              connection.close();
                            }
                            return true;
                        }catch (Exception ignored){
                            return false;
                        }
                    }
                };
                win.setCaption("Поправить оператора");
                UI.getCurrent().addWindow(win);
            }
        });

        newOp.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                TableEditorWindow win = new TableEditorWindow(table, null,"birthday", "startday", "endday"){
                    {
                        setNotNull("name");
                        setNotNull("surname");
                        setNotNull("grandname");
                        setNotNull("startday");
                    }
                    protected boolean actionPerformed(){
                       try{
                           Connection connection = DriverManager.getConnection("jdbc:sqlite:ivan.db3");
                           try {
                               PreparedStatement ps = connection.prepareStatement("insert into opers(name, surname, grandname, birthday, startday, endday, phone) values(?,?,?,?,?,?,?)");
                               ps.setString(1, getString("name"));
                               ps.setString(2, getString("surname"));
                               ps.setString(3, getString("grandname"));
                               ps.setDate(4, getDate("birthday"));
                               ps.setDate(5, getDate("startday"));
                               ps.setDate(6, getDate("endday"));
                               ps.setString(7, getString("phone"));
                               ps.execute();
                               ps.close();
                           }finally {
                               connection.close();
                           }
                           return true;
                       }catch(Exception ignored){
                           return false;
                       }
                    }
                };
                win.setCaption("Новый оператор");
                UI.getCurrent().addWindow(win);
            }
        });
    }
}
