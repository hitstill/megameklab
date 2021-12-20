/*
 * Copyright (c) 2021 - The MegaMek Team. All Rights Reserved.
 *
 * This program is  free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package megameklab.com.ui.combatVeh;

import megamek.common.*;
import megamek.common.verifier.TestTank;
import megameklab.com.ui.EntitySource;
import megameklab.com.ui.util.AbstractEquipmentDatabaseView;
import megameklab.com.util.UnitUtil;

import java.util.List;

import static megameklab.com.ui.util.EquipmentTableModel.*;

class CVEquipmentDatabaseView extends AbstractEquipmentDatabaseView {

    private final List<Integer> allColumns = List.of(COL_NAME, COL_DAMAGE, COL_DIVISOR, COL_SPECIAL, COL_HEAT,
            COL_MRANGE, COL_RANGE, COL_SHOTS, COL_TECH, COL_TLEVEL, COL_TRATING, COL_DPROTOTYPE, COL_DPRODUCTION,
            COL_DCOMMON, COL_DEXTINCT, COL_DREINTRO, COL_COST, COL_CREW, COL_BV, COL_TON, COL_CRIT, COL_REF);

    private final List<Integer> fluffColumns = List.of(COL_NAME, COL_TECH, COL_TLEVEL, COL_TRATING, COL_DPROTOTYPE,
            COL_DPRODUCTION, COL_DCOMMON, COL_DEXTINCT, COL_DREINTRO, COL_COST, COL_REF);

    private final List<Integer> statsColumns = List.of(COL_NAME, COL_DAMAGE, COL_HEAT, COL_MRANGE, COL_RANGE,
            COL_SHOTS, COL_TECH, COL_BV, COL_TON, COL_CRIT, COL_REF);

    CVEquipmentDatabaseView(EntitySource eSource) {
        super(eSource);
        updateVisibleColumns();
    }

    @Override
    protected void addEquipment(EquipmentType equip) {
        Mounted mount;
        boolean isMisc = equip instanceof MiscType;
        if (isMisc && equip.hasFlag(MiscType.F_TARGCOMP)) {
            if (!UnitUtil.hasTargComp(getTank())) {
                UnitUtil.updateTC(getTank(), equip);
            }
        } else {
            try {
                mount = new Mounted(getTank(), equip);
                int loc = Entity.LOC_NONE;
                if (isMisc && equip.hasFlag(MiscType.F_MAST_MOUNT)) {
                    loc = VTOL.LOC_ROTOR;
                } else if (TestTank.isBodyEquipment(equip)) {
                    loc = Tank.LOC_BODY;
                }
                getTank().addEquipment(mount, loc, false);
                if ((equip instanceof WeaponType) && equip.hasFlag(WeaponType.F_ONESHOT)) {
                    UnitUtil.removeOneShotAmmo(eSource.getEntity());
                }
            } catch (LocationFullException lfe) {
                // this can't happen, we add to Entity.LOC_NONE
            }
        }
    }

    @Override
    protected void updateVisibleColumns() {
        setColumnsVisible(allColumns, false);
        setColumnsVisible(tableMode ? statsColumns : fluffColumns, true);
    }

}
