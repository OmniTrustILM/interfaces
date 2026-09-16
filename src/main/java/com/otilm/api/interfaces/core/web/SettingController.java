package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.core.settings.BrandingSettingsDto;
import com.otilm.api.model.core.settings.BrandingSettingsUpdateDto;
import com.otilm.api.model.core.settings.EventSettingsDto;
import com.otilm.api.model.core.settings.EventsSettingsDto;
import com.otilm.api.model.core.settings.PlatformSettingsDto;
import com.otilm.api.model.core.settings.PlatformSettingsUpdateDto;
import com.otilm.api.model.core.settings.authentication.AuthenticationSettingsDto;
import com.otilm.api.model.core.settings.authentication.AuthenticationSettingsUpdateDto;
import com.otilm.api.model.core.settings.authentication.OAuth2ProviderSettingsResponseDto;
import com.otilm.api.model.core.settings.authentication.OAuth2ProviderSettingsUpdateDto;
import com.otilm.api.model.core.settings.logging.LoggingSettingsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/v1/settings")
@Tag(name = "Settings", description = "Settings API")
public interface SettingController extends AuthProtectedController {

    @Operation(summary = "Get platform settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Platform settings retrieved")})
    @GetMapping(path = "/platform", produces = MediaType.APPLICATION_JSON_VALUE)
    PlatformSettingsDto getPlatformSettings();

    @Operation(summary = "Update platform settings", description = """
            **Request semantics:** a section left out of the body (`utils`, `certificates`) is left untouched. A \
            `utils` that is present is stored as sent, so it carries the full desired state of that section: a field \
            left out is cleared, or returns to the platform default where it has one. Inside `certificates`, each \
            group (`validation`, `requestAttributes`, `registration`) behaves the same way as a section: one left out \
            is untouched, one that is present is stored as sent.""")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Settings updated")})
    @PutMapping(path = "/platform", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void updatePlatformSettings(@Valid @RequestBody PlatformSettingsUpdateDto platformSettingsDto);

    @Operation(summary = "Get platform branding",
            description = "Reads the branding category of the platform settings. Branding is also returned by "
                    + "`GET /v1/settings/platform`; only the write is split onto a dedicated endpoint, because reading "
                    + "it takes the same grant either way.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Platform branding retrieved")})
    @GetMapping(path = "/platform/branding", produces = MediaType.APPLICATION_JSON_VALUE)
    BrandingSettingsDto getBrandingSettings();

    /**
     * Separate from {@link #updatePlatformSettings} so that branding can be gated by its own {@code UPDATE_BRANDING}
     * action. Authorization is applied per method, so branding carried inside the platform update body would be
     * writable by anyone holding plain {@code UPDATE} over settings — which is exactly the grant this action exists to
     * split away. {@link PlatformSettingsUpdateDto} therefore has no branding field, while {@link PlatformSettingsDto}
     * still returns one: the read is the same grant either way.
     */
    @Operation(summary = "Update platform branding", description = """
            The only way to write branding.

            **Authorization:** gated by `UPDATE_BRANDING`, a narrower action than the `UPDATE` that gates the rest \
            of settings. Authorization is applied per endpoint. Branding is therefore deliberately absent from the \
            `PUT /v1/settings/platform` body. Carried there, it would be writable by anyone holding plain \
            `UPDATE`, leaving `UPDATE_BRANDING` nothing extra to grant.

            **Request semantics:** the request carries the full desired state. A field left out clears that part \
            of the branding.""")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Platform branding updated")})
    @PutMapping(path = "/platform/branding", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateBrandingSettings(@Valid @RequestBody BrandingSettingsUpdateDto brandingSettingsDto);

    @Operation(summary = "Get events settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Notification settings retrieved")})
    @GetMapping(path = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
    EventsSettingsDto getEventsSettings();

    @Operation(summary = "Update multiple events settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Setting updated")})
    @PutMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void updateEventsSettings(@Valid @RequestBody EventsSettingsDto eventsSettingsDto) throws NotFoundException;

    @Operation(summary = "Update single event settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Setting updated")})
    @PatchMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void updateEventSettings(@Valid @RequestBody EventSettingsDto eventSettingsDto) throws NotFoundException;

    @Operation(summary = "Get authentication settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authentication settings retrieved")})
    @GetMapping(path = "/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
    AuthenticationSettingsDto getAuthenticationSettings();

    @Operation(summary = "Update authentication settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authentication settings updated")})
    @PutMapping(path = "/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
    void updateAuthenticationSettings(@Valid @RequestBody AuthenticationSettingsUpdateDto authenticationSettingsDto);

    @Operation(summary = "Get OAuth2 Provider settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OAuth2 Provider settings retrieved")})
    @GetMapping(path = "/authentication/oauth2Providers/{providerName}", produces = MediaType.APPLICATION_JSON_VALUE)
    OAuth2ProviderSettingsResponseDto getOAuth2ProviderSettings(
            @Parameter(description = "OAuth2 Provider Name") @PathVariable String providerName);

    @Operation(summary = "Update OAuth2 Provider settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Setting updated")})
    @PutMapping(path = "/authentication/oauth2Providers/{providerName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateOAuth2ProviderSettings(
            @Parameter(description = "OAuth2 Provider Name") @PathVariable String providerName,
            @Valid @RequestBody OAuth2ProviderSettingsUpdateDto oauth2SettingsDto);

    @Operation(summary = "Remove OAuth2 Provider")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "OAuth2 Provider removed.")})
    @DeleteMapping(path = "/authentication/oauth2Providers/{providerName}")
    void removeOAuth2Provider(@Parameter(description = "OAuth2 Provider Name") @PathVariable String providerName);

    @Operation(summary = "Get logging settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Logging settings retrieved")})
    @GetMapping(path = "/logging", produces = MediaType.APPLICATION_JSON_VALUE)
    LoggingSettingsDto getLoggingSettings();

    @Operation(summary = "Update logging settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Logging settings updated")})
    @PutMapping(path = "/logging", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void updateLoggingSettings(@Valid @RequestBody LoggingSettingsDto loggingSettingsDto);
}
