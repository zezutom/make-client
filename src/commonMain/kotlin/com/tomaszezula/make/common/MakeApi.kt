package com.tomaszezula.make.common

import com.tomaszezula.make.common.model.*

/**
 * Accesses Make's fine-grained API.
 */
interface MakeApi {

    /**
     * Creates a new scenario.
     *
     * @param token
     * @param teamId
     * @param folderId
     * @param blueprintJson
     * @param scheduling
     *
     */
    suspend fun createScenario(
        token: AuthToken,
        teamId: Int,
        folderId: Int,
        blueprintJson: String,
        scheduling: Scheduling
    ): Result<Scenario>

    /**
     * Returns the scenario blueprint.
     *
     * @param scenarioId
     *
     */
    suspend fun getBlueprint(token: AuthToken, scenarioId: Int): Result<Blueprint>

    /**
     * Sets the user defined data of an arbitrary module within the provided scenario.
     *
     * @param scenarioId
     * @param moduleId
     * @param fieldName Determines the updated field.
     * @param data
     *
     */
    suspend fun setModuleData(
        token: AuthToken,
        scenarioId: Int,
        moduleId: Int,
        fieldName: String,
        data: Any
    ): Result<UpdateResult>
}
