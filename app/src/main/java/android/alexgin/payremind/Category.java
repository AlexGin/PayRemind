package android.alexgin.payremind;

public enum Category { // Категория платежа:
    UNKNOWN(0),  // Неизвестный
    BANK(1),     // Банковский (кредит)
    CAR(2),      // Авто (страховка, лизинг)
    HOUSE(3),    // Жилищно-Коммунальный
    MOBILE(4),   // Оплата услуг мобильной связи
    PHONE(5),    // Проводной связи
    TRAINING(6), // Обучение (тренировка)
    INSTALLMENT(7); // Рассрочка

    private final int value;
    private Category(int value) {
        this.value = value;
    }
}
