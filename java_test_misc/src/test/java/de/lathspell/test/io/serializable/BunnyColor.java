package de.lathspell.test.io.serializable;

/**
 * Nicht-Serialisierbarer Datentyp der in der Klasse Bunny benutzt wird.
 *
 * Als Beispiel f�r Klassen aus irgendwelchen libraries, bei denen man keine
 * Kontrolle dar�ber hat, ob sie Serializable implementieren oder nicht.
 */
class BunnyColor {

    private final String color;

    public BunnyColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return color;
    }

    public String getColor() {
        return color;
    }
}
