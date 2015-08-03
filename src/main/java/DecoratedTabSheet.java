import com.vaadin.server.Page;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;

/**
 * Created by Кира on 02.08.2015.
 */
public class DecoratedTabSheet extends TabSheet {
    public  DecoratedTabSheet(){
        setImmediate(true);
        addSelectedTabChangeListener(new SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if(getSelectedTab() instanceof  ExitFrom){
                    UI.getCurrent().getSession().setAttribute("login", null);
                    UI.getCurrent().getSession().setAttribute("password", null);
                    UI.getCurrent().close();
                    Page.getCurrent().getJavaScript().execute("window.location.href=window.location.href;");
                }
            }
        });
    }
}
