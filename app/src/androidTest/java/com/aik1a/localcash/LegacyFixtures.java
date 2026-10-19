package com.aik1a.localcash;

import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;

/** Semilla de compras ya realizadas en versiones anteriores, sin cargo retroactivo.
 * Solo tests descartables. Los pagos nuevos se verifican mediante CompraService. */
final class LegacyFixtures {
    static long realizada(LocalCashDbHelper helper, Compra compra) {
        Compra plan = new Compra(0,compra.getNombre(),Compra.Estado.PLANIFICADA,compra.getFecha(),
                compra.getContactoId(),compra.getDetalles(),compra.getDivisa());
        long id = new CompraRepository(helper).crear(plan);
        helper.getWritableDatabase().execSQL("UPDATE compras SET estado='REALIZADA' WHERE id="+id);
        return id;
    }
}
