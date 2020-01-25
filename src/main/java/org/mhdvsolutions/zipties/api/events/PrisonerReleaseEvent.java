/*
 * Zipties - Player restraint system.
 * Copyright (c) 2018, Mitchell Cook <https://github.com/Mishyy>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.mhdvsolutions.zipties.api.events;

import org.bukkit.entity.Player;
import org.mhdvsolutions.zipties.api.ReleaseType;

public final class PrisonerReleaseEvent extends PrisonerEvent {

    private final ReleaseType type;

    /**
     * Called when a prisoner is released from their cuffs (or if they escape from cuffs)
     *
     * @param prisoner   escaping/being released
     * @param releasedBy released/broken out by
     * @param type       whether the released was caused due to release, etc.
     */
    public PrisonerReleaseEvent(Player prisoner, Player releasedBy, ReleaseType type) {
        super(prisoner, releasedBy);
        this.type = type;

    }

    public ReleaseType getType() {
        return type;
    }

}
