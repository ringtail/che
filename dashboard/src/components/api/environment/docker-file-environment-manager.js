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

import {EnvironmentManager} from './environment-manager';
import {DockerfileParser} from './docker-file-parser';

/**
 * This is the implementation of environment manager that handles the docker file format of environment.
 *
 * Format sample and specific description:
 * //TODO
 *
 */
export class DockerFileEnvironmentManager extends EnvironmentManager {

  constructor() {
    super();

    this.parser = new DockerfileParser();
  }

  get editorMode() {
    return 'text/x-dockerfile';
  }

  /**
   * Parses a dockerfile and returns an array of objects
   *
   * @param content {string} content of dockerfile
   * @returns {Array} a list of instructions and arguments
   * @private
   */
  _parseRecipe(content) {
    let recipe = [];
    try {
      recipe = this.parser.parse(content);
    } catch (e) {
      console.error(e);
    }
    return recipe;
  }

  /**
   * Dumps a list of instructions and arguments into dockerfile
   *
   * @param instructions {array} array of objects
   * @returns {string} dockerfile
   * @private
   */
  _stringifyRecipe(instructions) {
    let content = '';

    try {
      content = this.parser.dump(instructions);
    } catch (e) {
      console.log(e);
    }

    return content;
  }

  /**
   * Provides the environment configuration based on machines format.
   *
   * @param environment origin of the environment to be edited
   * @param machines the list of machines
   * @returns environment's configuration
   */
  getEnvironment(environment, machines) {
    let newEnvironment = super.getEnvironment(environment, machines);

    // machines should contain one machine only
    if (machines[0].recipe) {
      try {
        newEnvironment.recipe.content = this._stringifyRecipe(machines[0].recipe);
      } catch (e) {
        console.error('Cannot update environment\'s recipe, error: ', e);
      }
    }

    return newEnvironment;
  }

  /**
   * Retrieves the list of machines.
   *
   * @param environment environment's configuration
   * @returns {Array} list of machines defined in environment
   */
  getMachines(environment) {
    let recipe = null,
        machines = [];

    if (environment.recipe.content) {
      recipe = this._parseRecipe(environment.recipe.content);
    }

    Object.keys(environment.machines).forEach((machineName) => {
      let machine = angular.copy(environment.machines[machineName]);
      machine.name = machineName;
      machine.recipe = recipe;

      machines.push(machine);
    });

    return machines;
  }

  /**
   * Returns a docker image from the recipe.
   *
   * @param machine
   * @returns {*}
   */
  getSource(machine) {
    if (!machine.recipe) {
      return null;
    }

    let from = machine.recipe.find((line) => {
      return line.instruction === 'FROM';
    });

    return {image: from.argument};
  }

  /**
   * Returns true if environment recipe content is present.
   *
   * @param machine {object}
   * @returns {boolean}
   */
  canEditEnvVariables(machine) {
    return !!machine.recipe;
  }

  /**
   * Returns environment variables from recipe
   *
   * @param machine {object}
   * @returns {*}
   */
  getEnvVariables(machine) {
    if (!machine.recipe) {
      return null;
    }

    let envVariables = {};

    let envList = machine.recipe.filter((line) => {
      return line.instruction === 'ENV';
    }) || [];

    envList.forEach((line) => {
      envVariables[line.argument[0]] = line.argument[1];
    });

    return envVariables;
  }

  /**
   * Updates machine with new environment variables.
   *
   * @param machine {object}
   * @param envVariables {object}
   */
  setEnvVariables(machine, envVariables) {
    if (!machine.recipe) {
      return;
    }

    let newRecipe = [];

    // remove all environment variables
    newRecipe = machine.recipe.filter((line) => {
      return line.instruction !== 'ENV';
    });

    // add environments if any
    if (Object.keys(envVariables).length) {
      Object.keys(envVariables).forEach((name) => {
        let line = {
          instruction: 'ENV',
          argument: [name, envVariables[name]]
        };
        newRecipe.splice(1,0,line);
      })
    }

    machine.recipe = newRecipe;
  }
}
