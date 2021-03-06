/*
 * This file is part of lidar-contrib, licensed under the GNU General Public License (GPLv3).
 *
 * Copyright (c) Armabot <https://www.armabot.com>
 * Copyright (c) contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.armabot.lidar.impl.vl53l1x;

class Calculations {

    static int encodeTimeout(int timeoutMclks) {
        if (timeoutMclks <= 0) {
            return 0;
        }

        long lsByte = timeoutMclks - 1;
        int msByte = 0;

        while ((lsByte & 0xFFFFFF00) > 0) {
            lsByte >>= 1;
            msByte++;
        }

        return (int) ((msByte << 8) | (lsByte & 0xFF));
    }

    static int decodeTimeout(int regVal) {
        return ((regVal & 0xFF) << (regVal >> 8)) + 1;
    }

    static int timeoutMclksToMicroseconds(int timeoutMclks, int macroPeriodMicrosec) {
        long tmp = ((long) timeoutMclks) * macroPeriodMicrosec;
        return (int) ((tmp + 0x800L) >> 12);
    }

    static int timeoutMicrosecondsToMclks(int timeoutMicrosec, int macroPeriodMicrosec) {
        return ((timeoutMicrosec << 12) + (macroPeriodMicrosec >> 1)) / macroPeriodMicrosec;
    }

    // "Calculate macro period in microseconds (12.12 format) with given VCSEL period"
    // TBH I have no idea what this does.
    static int calcMacroPeriod(int fastOscFreq, short vcselPeriod) {
        int pllPeriodMicrosec = (1 << 30) / fastOscFreq;
        int vcselPeriodPclks = (vcselPeriod + 1) << 1;
        int macroPeriodMicrosec = 2304 * pllPeriodMicrosec;
        macroPeriodMicrosec >>= 6;
        macroPeriodMicrosec *= vcselPeriodPclks;
        macroPeriodMicrosec >>= 6;
        return macroPeriodMicrosec;
    }
}
