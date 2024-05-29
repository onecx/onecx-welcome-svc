package org.tkit.onecx.welcome.domain.daos;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import org.tkit.onecx.welcome.domain.models.Image;
import org.tkit.onecx.welcome.domain.models.Image_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity_;

@ApplicationScoped
public class ImageDAO extends AbstractDAO<Image> {

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Stream<Image> findAllByWorkspaceName(String workspaceName) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Image.class);
            var root = cq.from(Image.class);

            List<Predicate> predicates = new ArrayList<>();
            addSearchStringPredicate(predicates, cb, root.get(Image_.WORKSPACE_NAME), workspaceName);

            cq.where(predicates.toArray(new Predicate[] {}));

            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.modificationDate)));

            return this.getEntityManager().createQuery(cq).getResultStream();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_IMAGE_INFO_BY_WORKSPACE, ex);
        }
    }

    enum ErrorKeys {
        ERROR_FIND_IMAGE_INFO_BY_WORKSPACE
    }
}
