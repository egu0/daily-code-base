package im.engure.unclassified;

import im.engure.recursive.PunishmentNumber2698;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PunishmentNumber2698Test {

    @Test
    void t1() {
        PunishmentNumber2698 o = new PunishmentNumber2698();
        Assertions.assertEquals(182, o.punishmentNumber(10));
    }

    @Test
    void t2() {
        PunishmentNumber2698 o = new PunishmentNumber2698();
        Assertions.assertEquals(1478, o.punishmentNumber(37));
    }

}