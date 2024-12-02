import java.awt.Desktop;
import java.net.URI;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        LinkManager manager = new LinkManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Введите UUID или нажмите Enter для генерации UUID: ");
            String userUuid = scanner.nextLine();
            if (userUuid.isBlank()) {
                userUuid = new User().getUuid();
                System.out.println("Ваш UUID: " + userUuid);
            }

            System.out.println("\nМеню:");
            System.out.println("1. Создать короткую ссылку");
            System.out.println("2. Посетить короткую ссылку");
            System.out.println("3. Очистить истекшие ссылки");
            System.out.println("4. Изменить лимит перехода ссылки");
            System.out.println("5. Удалить короткую ссылку");
            System.out.println("0. Выход");

            System.out.print("Выберите: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Введите оригинальный URL: ");
                    String originalUrl = scanner.nextLine();
                    System.out.print("Введите лимит переходов: ");
                    int clickLimit = scanner.nextInt();
                    System.out.print("Введите время работы ссылки в часах: ");
                    int expiryHours = scanner.nextInt();
                    scanner.nextLine();

                    String shortLink = manager.createShortLink(originalUrl, userUuid, clickLimit, expiryHours);
                    System.out.println("Коротная ссылка создана: " + shortLink);
                    break;

                case 2:
                    System.out.print("Введите короткую ссылку: ");
                    String shortUrl = scanner.nextLine();
                    String redirectUrl = manager.getOriginalUrl(shortUrl);
                    if (redirectUrl == null) {
                        System.out.println("Ссылка недоступна!");
                    } else {
                        manager.checkAndUpdateLink(shortUrl);
                        Desktop.getDesktop().browse(new URI(redirectUrl));
                    }
                    break;

                case 3:
                    manager.cleanExpiredLinks();
                    System.out.println("Ссылки очищены.");
                    break;

                case 4:
                    System.out.print("Введите короткую ссылку: ");
                    String currentShortUrl = scanner.nextLine();
                    System.out.print("Введите новый лимит переходов: ");
                    int newClickLimit = scanner.nextInt();
                    manager.changeLinkLimit(userUuid, currentShortUrl, newClickLimit);
                    System.out.print("Новый лимит установлен");
                    break;

                case 5:
                    System.out.print("Введите короткую ссылку: ");
                    String shortLinkToDelete = scanner.nextLine();
                    manager.deleteLink(userUuid, shortLinkToDelete);
                    System.out.print("Ссылка успешно удалена");
                    break;

                case 0:
                    System.exit(0);
                    return;

                default:
                    System.out.println("Неверная операция!");
            }
        }
    }
}
