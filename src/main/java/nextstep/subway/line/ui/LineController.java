package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.DuplicatedSectionException;
import nextstep.subway.NotFoundException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineRequest lineRequest) {
        final LineResponse line = saveLine(lineRequest);

        return ResponseEntity.created(createUri(line))
            .body(line);
    }

    private LineResponse saveLine(final LineRequest lineRequest) {
        return lineService.saveLine(lineRequest);
    }

    private URI createUri(final LineResponse line) {
        return URI.create("/lines/" + line.getId());
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable final Long id,
        @RequestBody final SectionRequest sectionRequest) {

        return ResponseEntity.ok(lineService.addSection(id, sectionRequest));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> searchAllLine() {
        final List<LineResponse> lineResponses = lineService.findLines();

        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> searchLine(@PathVariable final Long id) {
        final LineResponse lineResponse = lineService.findLine(id);

        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> modifyLine(@PathVariable final Long id,
        @RequestBody final LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);

        return ResponseEntity.ok()
            .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable final Long id) {
        lineService.deleteLineById(id);

        return ResponseEntity.noContent()
            .build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class, DuplicatedSectionException.class,
        IllegalArgumentException.class})
    public ResponseEntity<LineResponse> handleIllegalArgsException(final RuntimeException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<LineResponse> handleNotFoundException(final NotFoundException e) {
        return ResponseEntity.noContent()
            .build();
    }
}
