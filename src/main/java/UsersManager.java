import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.*;

import java.sql.*;

/**
 * Created by Кира on 02.08.2015.
 */
public class UsersManager extends VerticalLayout {
    private Container opContainer =  new HierarchicalContainer();
    private Table table;


    private Container roles = new HierarchicalContainer();
    private String[] rolesList = {"role1", "role2", "role3"};
    {
        roles.addContainerProperty("id", String.class, "");
        for(String s: rolesList) roles.addItem(s).getItemProperty("id").setValue(s);
    }


    private void reloadContainer(){
        try{
            opContainer.removeAllItems();
            Connection connection = DriverManager.getConnection("jdbc:sqlite:ivan.db3");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select id, login, password, role from users");
            while(rs.next()){
                Integer id = rs.getInt(1);
                Item item = opContainer.addItem(id);
                item.getItemProperty("id").setValue(id);
                item.getItemProperty("login").setValue(rs.getString(2));
                item.getItemProperty("password").setValue(rs.getString(3));
                item.getItemProperty("role").setValue(rs.getString(4));
            }
            rs.close();
            statement.close();
            connection.close();
        }catch (Exception ignored){

        }
    }

    public  UsersManager(){


        opContainer.addContainerProperty("id", Integer.class, null);
        opContainer.addContainerProperty("login", String.class, "");
        opContainer.addContainerProperty("password", String.class, "");
        opContainer.addContainerProperty("role", String.class, "");
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

        table.setVisibleColumns("login", "password", "role");
        table.setColumnHeaders("логин", "пароль", "роль");
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
                QueryWindow window = new QueryWindow("Точно удалить?", "delete from users where id = " + table.getValue());
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
                TableEditorWindow win = new TableEditorWindow(table, table.getValue()){
                    {
                      replaceToCombo("role", roles, rolesList[0]);
                    }
                    @Override
                    protected boolean actionPerformed() {
                        try {
                            Connection connection = DriverManager.getConnection("jdbc:sqlite:ivan.db3");
                            Integer id = (Integer)table.getValue();
                            try{
                                PreparedStatement ps = connection.prepareStatement("update users set login=?, password=?, role=? where id =" + id);
                                ps.setString(1, getString("login"));
                                ps.setString(2, getString("password"));
                                ps.setString(3, getString("role"));

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
                win.setCaption("Поправить пользователя");
                UI.getCurrent().addWindow(win);
            }
        });

        newOp.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                TableEditorWindow win = new TableEditorWindow(table, null){
                    {
                      replaceToCombo("role", roles, rolesList[0]);
                    }
                    @Override
                    protected boolean actionPerformed() {
                        try {
                            Connection connection = DriverManager.getConnection("jdbc:sqlite:ivan.db3");
                            try{
                                PreparedStatement ps = connection.prepareStatement("insert into users(login, password, role) values(?,?,?)");
                                ps.setString(1, getString("login"));
                                ps.setString(2, getString("password"));
                                ps.setString(3, getString("role"));

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
                win.setCaption("Создать пользователя");
                UI.getCurrent().addWindow(win);
            }
        });

    }

}
