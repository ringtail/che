/*
 * Copyright (c) 2015-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
'use strict';

/**
 * @ngdoc controller
 * @name list.environment.variables.controller:AddVariableDialogController
 * @description This class is handling the controller for the dialog box about adding the environment variable.
 * @author Oleksii Kurinnyi
 */
export class AddVariableDialogController {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($mdDialog, $scope) {
    this.$mdDialog = $mdDialog;
    this.updateInProgress = false;

    this.name = '';
    this.value = '';

    this.copyVariables = angular.copy(this.variables);
    delete this.copyVariables[this.name];

    let ctrl = this;
    // validate environment name uniqueness
    $scope.isUnique = (name) => {
      return !ctrl.copyVariables[name];
    }
  }

  /**
   * It will hide the dialog box.
   */
  hide() {
    this.$mdDialog.hide();
  }

  /**
   * Adds new environment variable
   */
  addVariable() {
    this.updateInProgress = true;

    this.callbackController.updateEnvVariable(this.name, this.value).finally(() => {
      this.updateInProgress = false;
      this.hide();
    });
  }

}
