import com.vaadin.server.Page;
import com.vaadin.ui.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * Created by Кира on 01.08.2015.
 */
public class AuthorizationForm extends VerticalLayout {
    public  AuthorizationForm(){
        final TextField user = new TextField();
        addComponent(user);
        final PasswordField  password = new PasswordField();
        addComponent(password);
        Button go = new Button("Войти");
        addComponent(go);
        go.setId("clickme");
        user.setImmediate(true);
        password.setImmediate(true);
        go.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
               String role = getRole(user.getValue(), password.getValue());

               if(role!=null){
                   UI.getCurrent().getSession().setAttribute("login", user.getValue());
                   UI.getCurrent().getSession().setAttribute("password", password.getValue());
                   TabSheet sheet = new DecoratedTabSheet();
                   UI.getCurrent().setContent(sheet);
                   sheet.addTab(new OperInput(), "Операторы");
                   sheet.addTab(new VerticalLayout(), "Станки");
                   sheet.addTab(new ExitFrom(), "Выход - " + user.getValue());


                   //TODO тут должно быть ветвление для каждой роли и чтение из базы, кто есть кто
               }else{
                   UI.getCurrent().getSession().setAttribute("login", null);
                   UI.getCurrent().getSession().setAttribute("password", null);

                   user.setValue("");
                   password.setValue("");
               }

            }
        });

        Object o1 = UI.getCurrent().getSession().getAttribute("login");
        Object o2 = UI.getCurrent().getSession().getAttribute("password");
        if(o1!=null && o2!=null){
            user.setValue((String)o1);
            password.setValue((String)o2);
            Page.getCurrent().getJavaScript().execute("document.getElementById('clickme').click();");
        }

    }

    private String getRole(String login, String password) {
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:ivan.db3");
            try{
                PreparedStatement ps = connection.prepareStatement("select role from users where login = ? and password = ?");
                ps.setString(1, login);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                rs.next();
                return rs.getString(1);
            }finally{
                connection.close();
            }
        }catch (Exception ex){

        }
        return  null;
    }
}
