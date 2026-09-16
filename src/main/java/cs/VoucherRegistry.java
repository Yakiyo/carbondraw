package cs;

import java.util.List;

public class VoucherRegistry {
    public static final List<Voucher> VOUCHERS = List.of(
        new Voucher("Antimatter", "+1 Joker Slot", Voucher.EffectType.EXTRA_JOKER_SLOT, "/cs/images/vouchers/Antimatter.png"),
        new Voucher("Clearance Sale", "All cards and packs in shop are 25% off", Voucher.EffectType.CLEARANCE_SALE, "/cs/images/vouchers/Clearance_Sale.png"),
        new Voucher("Grabber", "+1 hand per round", Voucher.EffectType.EXTRA_HAND, "/cs/images/vouchers/Grabber.png"),
        new Voucher("Overstock", "+1 card slot available in shop", Voucher.EffectType.OVERSTOCK, "/cs/images/vouchers/Overstock.png"),
        new Voucher("Reroll Surplus", "Rerolls cost less", Voucher.EffectType.REROLL_SURPLUS, "/cs/images/vouchers/Reroll_Surplus.png"),
        new Voucher("Wasteful", "+1 discard per round", Voucher.EffectType.EXTRA_DISCARD, "/cs/images/vouchers/Wasteful.png")
    );

    public static Voucher getByName(String name) {
        for (Voucher v : VOUCHERS) {
            if (v.name().equals(name)) return v;
        }
        return null;
    }
}
