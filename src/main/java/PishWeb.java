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

        TabSheet sheet = new TabSheet();
        UI.getCurrent().setContent(sheet);
        sheet.addTab(new OperInput(), "Операторы");
        sheet.addTab(new VerticalLayout(), "Станки");

        String address = Page.getCurrent().getWebBrowser().getAddress();
        System.out.println(address);
       // Page.getCurrent().getJavaScript().execute("setInterval(function(){setTimeout(function(){autoupdater();},10);},3000);");
    }
}