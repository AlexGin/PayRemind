package android.alexgin.payremind;

public enum Category { // Категория платежа:
    UNKNOWN(0),  // Неизвестный
    BANK(1),     // Банковский (кредит)
    CAR(2),      // Авто (страховка, лизинг)
    HOUSE(3),    // Жилищно-Коммунальный платёж
    MOBILE(4),   // Оплата услуг мобильной связи
    PHONE(5),    // Оплата услуг проводной связи
    TAX(6),      // Оплата налога (налогов)
    TRAINING(7),    // Обучение (тренировка)
    INSTALLMENT(8); // Рассрочка

    private final int value;
    private Category(int value) {
        this.value = value;
    }
}
