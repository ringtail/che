/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.api.machine;

import org.eclipse.che.api.core.model.machine.Machine;

import java.util.Map;

/**
 * Defines machine entity on client side.
 *
 * @author Roman Nikitenko
 */
public interface MachineEntity extends Machine {

    boolean isDev();

    /** @return type of current machine */
    String getType();

    /** @return current machine's display name */
    String getDisplayName();

    /** Returns information about machine. */
    Map<String, String> getProperties();

    String getTerminalUrl();
}
