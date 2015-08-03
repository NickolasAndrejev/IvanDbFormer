/**
 * Created by virtual on 20.06.2015.
 */
import com.vaadin.annotations.Title;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Title("Заполнение таблиц")
public class PishWeb extends UI {
    public PishWeb() {
    }

    protected void init(VaadinRequest request) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
        }

        String ip = Page.getCurrent().getWebBrowser().getAddress();
        if("127.0.0.1".equals(ip)) {
            TabSheet sheet = new DecoratedTabSheet();
            UI.getCurrent().setContent(sheet);

            sheet.addTab(new OperInput(), "Операторы");
            sheet.addTab(new VerticalLayout(), "Станки");
            sheet.addTab(new UsersManager(), "Пользователи");



        }else{
            UI.getCurrent().setContent(new AuthorizationForm());
        }

       // Page.getCurrent().getJavaScript().execute("setInterval(function(){setTimeout(function(){autoupdater();},10);},3000);");
    }
}