import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by virtual on 07.07.2015.
 */
public class QueryWindow extends ActionWindow {
    public QueryWindow(String message, final String query){
        Label label = new Label(message);
        Button b1 =new Button("Да");
        Button b2 = new Button("Нет");
        setContent(new VerticalLayout(label, new HorizontalLayout(b1,b2)));
        b1.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    Connection connection = DriverManager.getConnection("jdbc:sqlite:ivan.db3");
                    try {
                        Statement statement = connection.createStatement();
                        statement.execute(query);
                    } finally {
                        connection.close();
                    }
                } catch (Exception ignored) {

                }
                close();
            }
        });
        b2.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
            }
        });
    }
}
