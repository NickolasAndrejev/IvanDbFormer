import com.vaadin.server.Page;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * Created by virtual on 20.06.2015.
 */
//После закрытия окна происходит нажатие на элемент с ID = refresh_button
public class ActionWindow  extends Window {
    public  ActionWindow(){
        addCloseListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent closeEvent) {
                Page.getCurrent().getJavaScript().execute("document.getElementById('refresh_button').click();");
            }
        });
    }
}
