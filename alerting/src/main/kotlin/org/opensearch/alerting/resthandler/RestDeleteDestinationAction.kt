/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.alerting.resthandler

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.opensearch.action.support.WriteRequest
import org.opensearch.alerting.AlertingPlugin
import org.opensearch.alerting.action.DeleteDestinationAction
import org.opensearch.alerting.action.DeleteDestinationRequest
import org.opensearch.alerting.util.REFRESH
import org.opensearch.client.node.NodeClient
import org.opensearch.rest.BaseRestHandler
import org.opensearch.rest.BaseRestHandler.RestChannelConsumer
import org.opensearch.rest.RestHandler.ReplacedRoute
import org.opensearch.rest.RestHandler.Route
import org.opensearch.rest.RestRequest
import org.opensearch.rest.action.RestToXContentListener
import java.io.IOException

private val log: Logger = LogManager.getLogger(RestDeleteDestinationAction::class.java)

/**
 * This class consists of the REST handler to delete destination.
 */
class RestDeleteDestinationAction : BaseRestHandler() {

    override fun getName(): String {
        return "delete_destination_action"
    }

    override fun routes(): List<Route> {
        return listOf()
    }

    override fun replacedRoutes(): MutableList<ReplacedRoute> {
        return mutableListOf(
            ReplacedRoute(
                RestRequest.Method.DELETE,
                "${AlertingPlugin.DESTINATION_BASE_URI}/{destinationID}",
                RestRequest.Method.DELETE,
                "${AlertingPlugin.LEGACY_OPENDISTRO_DESTINATION_BASE_URI}/{destinationID}"
            )
        )
    }

    @Throws(IOException::class)
    override fun prepareRequest(request: RestRequest, client: NodeClient): RestChannelConsumer {
        log.debug("${request.method()} ${AlertingPlugin.DESTINATION_BASE_URI}/{destinationID}")

        val destinationId = request.param("destinationID")
        log.debug("${request.method()} ${AlertingPlugin.MONITOR_BASE_URI}/$destinationId")

        val refreshPolicy = WriteRequest.RefreshPolicy.parse(request.param(REFRESH, WriteRequest.RefreshPolicy.IMMEDIATE.value))
        val deleteDestinationRequest = DeleteDestinationRequest(destinationId, refreshPolicy)

        return RestChannelConsumer { channel ->
            client.execute(DeleteDestinationAction.INSTANCE, deleteDestinationRequest, RestToXContentListener(channel))
        }
    }
}
