package github;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class Gui {

    private final TrayIcon trayIcon;

    public Gui() {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/GitHub-Mark.png"));
        trayIcon = new TrayIcon(image, "Github helper");
        trayIcon.setImageAutoSize(true);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void showNotification(String title, String text) {
        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
    }

    public void setMenu(String login, List<RepositoryDescription> repos) {
        PopupMenu popup = new PopupMenu();
        MenuItem accountMI = new MenuItem(login);
        accountMI.addActionListener(e -> openInBrowser("https://github.com/" + login));

        MenuItem notificationMI = new MenuItem("notifications");
        accountMI.addActionListener(e -> openInBrowser("https://github.com/notifications"));

        Menu repositoriesMI = new Menu("repositories");
        repos.forEach(repo -> {
            String name = repo.getPullRequests().isEmpty()
                ? repo.getName()
                : String.format("(%d) %s", repo.getPullRequests().size(), repo.getName());
            Menu repoSM = new Menu(name);

            MenuItem openInBrowser = new MenuItem("Open in browser");
            openInBrowser.addActionListener(e -> openInBrowser(repo.getRepository().getHtmlUrl().toString()));

            repoSM.add(openInBrowser);

            if (!repos.isEmpty()) {
                repoSM.addSeparator();
            }

            repo.getPullRequests().forEach(pr -> {
                MenuItem prMI = new MenuItem(pr.getTitle());
                prMI.addActionListener(e -> openInBrowser(pr.getHtmlUrl().toString()));
                repoSM.add(prMI);
            });

            repositoriesMI.add(repoSM);
        });

        popup.add(accountMI);
        popup.addSeparator();
        popup.add(notificationMI);
        popup.add(repositoriesMI);

        trayIcon.setPopupMenu(popup);
    }

    public void openInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
